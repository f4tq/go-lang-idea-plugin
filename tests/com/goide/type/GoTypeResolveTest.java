/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goide.type;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.psi.GoType;
import com.goide.psi.GoTypeOwner;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import org.jetbrains.annotations.NotNull;

public class GoTypeResolveTest extends GoCodeInsightFixtureTestCase {
  public void testAnon() {
    doTopLevelTest("type A struct{};type E A;type B struct{ E };func (e E) foo() {};func main() { b := B{}; b.<caret>E }", "E A");
  }

  public void testTypeSwitchDeclaration() {
    doStatementTest("switch fo<caret>o := \"hello\".(type) {}", "string");
  }

  public void testTypeSwitchUsageInContext() {
    doStatementTest("switch foo := \"hello\".(type) { case bool:\n println(fo<caret>o)\n}", "bool");
  }

  public void testWrappedSlice() {
    doTopLevelTest("type Foo int[]\nfunc _() { var foo Foo\nb<caret>ar := foo[2:9]", "Foo");
  }

  public void testSlice() {
    doStatementTest("var foo []int\nb<caret>ar := foo[2:9]", "[]int");
  }
  
  public void testRangeOverString() {
    doStatementTest("for fo<caret>o := range \"hello\" {}", "int");
  }

  public void testNestedTypeSwitchUsageInContext() {
    doStatementTest("var p interface{}\n" +
                    "switch foo := p.(type) {\n" +
                    "case int:\n" +
                    "  switch p.(type) {\n" +
                    "  case bool:" +
                    "    println(f<caret>oo)\n" +
                    "  }\n" +
                    "}", "int");
  }

  private void doTopLevelTest(@NotNull String text, @NotNull String expectedTypeText) {
    myFixture.configureByText("a.go", "package a;" + text);
    PsiElement elementAt;
    SelectionModel selectionModel = myFixture.getEditor().getSelectionModel();
    if (selectionModel.hasSelection()) {
      PsiElement left = myFixture.getFile().findElementAt(selectionModel.getSelectionStart());
      PsiElement right = myFixture.getFile().findElementAt(selectionModel.getSelectionEnd());
      assertNotNull(left);
      assertNotNull(right);
      elementAt = PsiTreeUtil.findCommonParent(left, right);
    }
    else {
      elementAt = myFixture.getFile().findElementAt(myFixture.getEditor().getCaretModel().getOffset());
    }
    assertNotNull(elementAt);

    GoTypeOwner typeOwner = PsiTreeUtil.getNonStrictParentOfType(elementAt, GoTypeOwner.class);
    assertNotNull("Cannot find type owner. Context element: " + elementAt.getText(), typeOwner);

    GoType type = typeOwner.getGoType(null);
    assertEquals(expectedTypeText, type == null ? "<unknown>" : type.getText());
  }

  private void doStatementTest(@NotNull String text, @NotNull String expectedTypeText) {
    doTopLevelTest("func _() {\n" + text + "\n}", expectedTypeText);
  }
  
  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return createMockProjectDescriptor();
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    setUpProjectSdk();
  }
}
