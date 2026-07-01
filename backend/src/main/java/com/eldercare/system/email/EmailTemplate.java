package com.eldercare.system.email;

/**
 * 验证码邮件 HTML 模板。
 *
 * <p>注册、找回密码等场景共用同一套模板，通过 action 文案区分用途。
 * 采用 table + 内联 style 布局，兼容 QQ 邮箱、网易、Gmail 等主流客户端。
 */
public class EmailTemplate {

    /** 品牌主色（医疗青蓝） */
    private static final String PRIMARY_COLOR = "#2563eb";

    /** 品牌名 */
    private static final String BRAND_NAME = "东软颐养中心";

    private EmailTemplate() {
    }

    /**
     * 生成验证码邮件 HTML 正文。
     *
     * @param action 操作描述，如"注册账号"、"找回密码"
     * @param code   6 位验证码
     * @return HTML 字符串
     */
    public static String buildVerifyCodeHtml(String action, String code) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html lang=\"zh-CN\">");
        sb.append("<head><meta charset=\"UTF-8\"></head>");
        sb.append("<body style=\"margin:0;padding:0;background-color:#f4f5f7;");
        sb.append("font-family:-apple-system,BlinkMacSystemFont,'Segoe UI','PingFang SC','Microsoft YaHei',sans-serif;\">");

        // 外层卡片
        sb.append("<table align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" ");
        sb.append("style=\"width:600px;max-width:100%;margin:32px auto;background-color:#ffffff;");
        sb.append("border-radius:8px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,0.06);\">");

        // 顶部品牌条
        sb.append("<tr><td style=\"padding:24px 32px;background-color:" + PRIMARY_COLOR + ";\">");
        sb.append("<span style=\"font-size:20px;font-weight:600;color:#ffffff;letter-spacing:1px;\">");
        sb.append(BRAND_NAME);
        sb.append("</span></td></tr>");

        // 正文区
        sb.append("<tr><td style=\"padding:32px;\">");
        sb.append("<p style=\"margin:0 0 16px;font-size:14px;color:#4b5563;line-height:1.6;\">您好，</p>");
        sb.append("<p style=\"margin:0 0 24px;font-size:14px;color:#4b5563;line-height:1.6;\">");
        sb.append("您正在进行<strong style=\"color:#111827;\">" + action + "</strong>操作，验证码为：</p>");

        // 验证码色块
        sb.append("<table cellpadding=\"0\" cellspacing=\"0\" align=\"center\" style=\"margin:0 auto 24px;\">");
        sb.append("<tr><td style=\"padding:16px 40px;background-color:" + PRIMARY_COLOR + ";border-radius:6px;\">");
        sb.append("<span style=\"font-size:32px;font-weight:700;color:#ffffff;letter-spacing:8px;\">" + code + "</span>");
        sb.append("</td></tr></table>");

        sb.append("<p style=\"margin:0 0 8px;font-size:14px;color:#4b5563;line-height:1.6;\">验证码 5 分钟内有效，请尽快完成验证。</p>");
        sb.append("<p style=\"margin:0;font-size:12px;color:#9ca3af;line-height:1.6;\">如非本人操作，请忽略此邮件。</p>");
        sb.append("</td></tr>");

        // 底部
        sb.append("<tr><td style=\"padding:16px 32px;border-top:1px solid #e5e7eb;background-color:#fafafa;\">");
        sb.append("<p style=\"margin:0;font-size:12px;color:#9ca3af;line-height:1.6;\">此为系统邮件，请勿直接回复</p>");
        sb.append("<p style=\"margin:4px 0 0;font-size:12px;color:#9ca3af;line-height:1.6;\">© " + BRAND_NAME + "</p>");
        sb.append("</td></tr>");

        sb.append("</table>");
        sb.append("</body></html>");

        return sb.toString();
    }
}
