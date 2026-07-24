package com.crm.controller;

import com.crm.dto.CompanySettingsForm;
import com.crm.entity.CompanySettings;
import com.crm.service.BackupService;
import com.crm.service.CompanySettingsService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 公司資料與預設值設定,以及資料庫備份操作。
 */
@Controller
public class SettingsController {

    private static final List<String> CURRENCY_OPTIONS = List.of("TWD", "USD", "CNY", "EUR", "JPY");
    private final CompanySettingsService settingsService;
    private final BackupService backupService;

    public SettingsController(CompanySettingsService settingsService, BackupService backupService) {
        this.settingsService = settingsService;
        this.backupService = backupService;
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("activeMenu", "settings");
        CompanySettings s = settingsService.getOrCreate();
        if (!model.containsAttribute("settingsForm")) {
            model.addAttribute("settingsForm", toForm(s));
        }
        model.addAttribute("currentLogo", s.getLogoPath());
        model.addAttribute("currencyOptions", CURRENCY_OPTIONS);
        addBackupInfo(model);
        return "settings/index";
    }

    /** 儲存公司設定(含選擇性的 Logo 上傳) */
    @PostMapping("/settings")
    public String save(@Valid @ModelAttribute("settingsForm") CompanySettingsForm form,
                       BindingResult result,
                       @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("activeMenu", "settings");
            model.addAttribute("currentLogo", settingsService.getOrCreate().getLogoPath());
            model.addAttribute("currencyOptions", CURRENCY_OPTIONS);
            addBackupInfo(model);
            return "settings/index";
        }
        CompanySettings s = settingsService.getOrCreate();
        s.setCompanyName(form.getCompanyName());
        s.setTaxId(form.getTaxId());
        s.setAddress(form.getAddress());
        s.setPhone(form.getPhone());
        s.setFax(form.getFax());
        s.setEmail(form.getEmail());
        s.setContactName(form.getContactName());
        s.setDefaultCurrency(form.getDefaultCurrency());
        s.setDefaultTaxRate(form.getDefaultTaxRate());
        s.setDefaultPaymentTerms(form.getDefaultPaymentTerms());
        s.setDefaultDeliveryTerms(form.getDefaultDeliveryTerms());
        s.setContactReminderDays(form.getContactReminderDays());
        s.setQuotationFollowupDays(form.getQuotationFollowupDays());

        // 處理 Logo 上傳
        if (logoFile != null && !logoFile.isEmpty()) {
            s.setLogoPath(storeLogo(logoFile));
        }
        settingsService.save(s);
        ra.addFlashAttribute("flashSuccess", "公司設定已儲存");
        return "redirect:/settings";
    }

    /** 立即備份 */
    @PostMapping("/settings/backup")
    public String backup(RedirectAttributes ra) {
        try {
            String name = backupService.backupNow();
            ra.addFlashAttribute("flashSuccess", "備份完成:" + name);
        } catch (RuntimeException e) {
            ra.addFlashAttribute("flashError", "備份失敗:" + e.getMessage());
        }
        return "redirect:/settings";
    }

    // ===== 輔助 =====

    private void addBackupInfo(Model model) {
        model.addAttribute("lastBackup", backupService.lastBackupName());
        model.addAttribute("backupList", backupService.listBackupFiles().stream()
                .map(p -> p.getFileName().toString()).limit(30).toList());
        model.addAttribute("backupDir", backupService.backupDirAbsolutePath());
    }

    /** 儲存 Logo 到 uploads 資料夾,回傳檔名 */
    private String storeLogo(MultipartFile file) {
        try {
            Path uploads = Path.of("uploads");
            Files.createDirectories(uploads);
            String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "logo" : file.getOriginalFilename());
            String ext = StringUtils.getFilenameExtension(original);
            String safeExt = (ext == null || ext.isBlank()) ? "png" : ext.toLowerCase();
            // 固定檔名格式,避免目錄穿越(不採用使用者原始檔名)
            String name = "logo_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "." + safeExt;
            Path target = uploads.resolve(name).normalize();
            // 確認仍在 uploads 目錄內
            if (!target.toAbsolutePath().startsWith(uploads.toAbsolutePath())) {
                throw new IllegalArgumentException("不合法的檔案路徑");
            }
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return name;
        } catch (IOException e) {
            throw new UncheckedIOException("Logo 上傳失敗", e);
        }
    }

    private CompanySettingsForm toForm(CompanySettings s) {
        CompanySettingsForm f = new CompanySettingsForm();
        f.setCompanyName(s.getCompanyName());
        f.setTaxId(s.getTaxId());
        f.setAddress(s.getAddress());
        f.setPhone(s.getPhone());
        f.setFax(s.getFax());
        f.setEmail(s.getEmail());
        f.setContactName(s.getContactName());
        f.setDefaultCurrency(s.getDefaultCurrency());
        f.setDefaultTaxRate(s.getDefaultTaxRate());
        f.setDefaultPaymentTerms(s.getDefaultPaymentTerms());
        f.setDefaultDeliveryTerms(s.getDefaultDeliveryTerms());
        f.setContactReminderDays(s.getContactReminderDays());
        f.setQuotationFollowupDays(s.getQuotationFollowupDays());
        return f;
    }
}
