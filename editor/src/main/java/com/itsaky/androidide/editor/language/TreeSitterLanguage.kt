/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.itsaky.androidide.editor.language

import android.content.Context
import com.itsaky.androidide.editor.schemes.IDEColorSchemeProvider
import com.itsaky.androidide.editor.schemes.LanguageSpecProvider
import com.itsaky.androidide.editor.schemes.LanguageSpecProvider.getLanguageSpec
import com.itsaky.androidide.editor.schemes.LocalCaptureSpecProvider
import com.itsaky.androidide.editor.schemes.LocalCaptureSpecProvider.newLocalCaptureSpec
import com.itsaky.androidide.treesitter.TSLanguage
import io.github.rosemoe.sora.editor.ts.TsAnalyzeManager
import io.github.rosemoe.sora.editor.ts.TsLanguageSpec
import io.github.rosemoe.sora.editor.ts.TsTheme
import io.github.rosemoe.sora.lang.analysis.AnalyzeManager

/**
 * Tree Sitter language implementation.
 *
 * @author Akash Yadav
 */
abstract class TreeSitterLanguage(
  context: Context,
  lang: TSLanguage,
  type: String
) : IDELanguage() {

  private lateinit var tsTheme: TsTheme
  private lateinit var languageSpec: TsLanguageSpec
  private val analyzer by lazy { TsAnalyzeManager(languageSpec, tsTheme) }

  init {
    this.languageSpec = getLanguageSpec(context, type, lang, newLocalCaptureSpec(type))
    this.tsTheme = TsTheme(languageSpec.tsQuery)
    IDEColorSchemeProvider.readScheme { scheme ->
      val langScheme =
        checkNotNull(scheme.languages[type]) { "No color scheme found for file type '$type'" }
      langScheme.styles.forEach { tsTheme.putStyleRule(it.key, it.value.makeStyle()) }
    }
  }

  override fun getAnalyzeManager(): AnalyzeManager {
    return this.analyzer
  }

  override fun destroy() {
    languageSpec.close()
  }
}
