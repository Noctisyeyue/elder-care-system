import katex from 'katex'
import 'katex/dist/katex.min.css'

/**
 * 预处理数学公式，将中文括号转换为LaTeX格式
 * @param text 原始文本
 * @returns 预处理后的文本
 */
function preprocessMath(text: string): string {
  if (!text) return text

  return (
    text
      // 将中文括号转换为LaTeX格式
      .replace(/（([^）]+)）/g, '($1)') // 中文圆括号 -> LaTeX圆括号

      // 处理方括号格式：[...] -> \[...\]
      .replace(/\[([^\]]+)\]/g, '\\[$1\\]')

      // 修复一些常见的数学符号格式
      .replace(/\\frac\{([^}]+)\}\{([^}]+)\}/g, '\\frac{$1}{$2}') // 确保分数格式正确
      .replace(/\\ln/g, '\\ln') // 确保对数符号正确
      .replace(/\\neq/g, '\\neq') // 确保不等号正确

      // 修复导数符号
      .replace(/\\frac\{d\}\{dx\}/g, '\\frac{d}{dx}') // 导数符号
      .replace(/x\^\{([^}]+)\}/g, 'x^{$1}') // 幂次格式
      .replace(/e\^x/g, 'e^x') // 指数格式
      .replace(/a\^x/g, 'a^x')
  ) // 指数格式
}

/**
 * 渲染LaTeX数学公式
 * @param text 包含LaTeX公式的文本
 * @returns 渲染后的HTML字符串
 */
export function renderMath(text: string): string {
  if (!text) return text

  // 先预处理文本
  text = preprocessMath(text)

  // 处理行内公式 \( ... \)
  text = text.replace(/\\\((.*?)\\\)/g, (match, formula) => {
    try {
      const cleanFormula = formula.trim()
      // console.log('渲染行内公式:', cleanFormula)
      return katex.renderToString(cleanFormula, {
        displayMode: false,
        throwOnError: false,
        errorColor: '#cc0000',
      })
    } catch (error) {
      console.warn('KaTeX行内公式渲染错误:', error, '原始公式:', formula)
      return `<span style="color: #cc0000; font-family: monospace;">${match}</span>`
    }
  })

  // 处理行间公式 \[ ... \]
  text = text.replace(/\\\[(.*?)\\\]/g, (match, formula) => {
    try {
      const cleanFormula = formula.trim()
      // console.log('渲染行间公式:', cleanFormula)
      return katex.renderToString(cleanFormula, {
        displayMode: true,
        throwOnError: false,
        errorColor: '#cc0000',
      })
    } catch (error) {
      console.warn('KaTeX行间公式渲染错误:', error, '原始公式:', formula)
      return `<div style="color: #cc0000; font-family: monospace; text-align: center; margin: 10px 0;">${match}</div>`
    }
  })

  // 处理美元符号公式 $ ... $ (行内)
  text = text.replace(/\$([^$\n]+?)\$/g, (match, formula) => {
    try {
      const cleanFormula = formula.trim()
      // console.log('渲染美元符号行内公式:', cleanFormula)
      return katex.renderToString(cleanFormula, {
        displayMode: false,
        throwOnError: false,
        errorColor: '#cc0000',
      })
    } catch (error) {
      console.warn('KaTeX美元符号行内公式渲染错误:', error, '原始公式:', formula)
      return `<span style="color: #cc0000; font-family: monospace;">${match}</span>`
    }
  })

  // 处理双美元符号公式 $$ ... $$ (行间)
  text = text.replace(/\$\$([^$\n]+?)\$\$/g, (match, formula) => {
    try {
      const cleanFormula = formula.trim()
      // console.log('渲染美元符号行间公式:', cleanFormula)
      return katex.renderToString(cleanFormula, {
        displayMode: true,
        throwOnError: false,
        errorColor: '#cc0000',
      })
    } catch (error) {
      console.warn('KaTeX美元符号行间公式渲染错误:', error, '原始公式:', formula)
      return `<div style="color: #cc0000; font-family: monospace; text-align: center; margin: 10px 0;">${match}</div>`
    }
  })

  // 处理一些常见的格式问题
  text = text
    .replace(/\\mathbf\{([^}]+)\}/g, '\\mathbf{$1}') // 修复\mathbf格式
    .replace(/\\partial/g, '\\partial') // 确保\partial正确显示
    .replace(/\\mathrm\{([^}]+)\}/g, '\\mathrm{$1}') // 修复\mathrm格式

  return text
}

/**
 * 检查文本是否包含数学公式
 * @param text 要检查的文本
 * @returns 是否包含数学公式
 */
export function hasMathFormula(text: string): boolean {
  if (!text) return false

  const mathPatterns = [
    /\\\(.*?\\\)/, // \( ... \)
    /\\\[.*?\\\]/, // \[ ... \]
    /\$[^$\n]+?\$/, // $ ... $
    /\$\$[^$\n]+?\$\$/, // $$ ... $$
    /（[^）]+）/g, // 中文括号
    /\[[^\]]+\]/g, // 方括号
  ]

  return mathPatterns.some((pattern) => pattern.test(text))
}

/**
 * 验证LaTeX公式语法
 * @param formula LaTeX公式
 * @returns 是否语法正确
 */
export function validateLatexFormula(formula: string): boolean {
  try {
    katex.renderToString(formula, {
      displayMode: false,
      throwOnError: true,
    })
    return true
  } catch (error) {
    console.warn('LaTeX语法错误:', error)
    return false
  }
}
