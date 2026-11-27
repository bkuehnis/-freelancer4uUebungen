package ch.zhaw.freelancer4u.model;

import java.util.List;

public record MailInformation(Boolean format, Boolean disposable, Boolean dns, List<String> signals) {

    public boolean isDnsIndeterminate() {
        return signals != null && signals.contains("dns_indeterminate");
    }

    public boolean isValid() {
        return Boolean.TRUE.equals(format) && Boolean.FALSE.equals(disposable) && Boolean.TRUE.equals(dns);
    }
}
