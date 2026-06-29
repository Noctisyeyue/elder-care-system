import { renderMath } from './mathRenderer'
import { renderMarkdown } from './markdownRenderer'

/**
 * 综合内容渲染器
 * 先渲染Markdown，再渲染数学公式
 * @param text 原始文本
 * @returns 渲染后的HTML字符串
 */
export function renderContent(text: string): string {
  if (!text) return text

  try {
    // 先渲染Markdown
    let renderedText = renderMarkdown(text)

    // 再渲染数学公式
    renderedText = renderMath(renderedText)

    return renderedText
  } catch (error) {
    console.warn('内容渲染错误:', error)
    return text
  }
}

/**
 * 检查文本是否需要渲染
 * @param text 要检查的文本
 * @returns 是否需要渲染
 */
export function needsRendering(text: string): boolean {
  if (!text) return false

  // 检查是否包含数学公式或Markdown标记
  const hasMath = /\\\(|\\\[|\$|\\mathbf|\\nabla|\\int|\\sum/.test(text)
  const hasMarkdown = /\*\*|\*|#+\s|- |\d+\. |`/.test(text)

  return hasMath || hasMarkdown
}
