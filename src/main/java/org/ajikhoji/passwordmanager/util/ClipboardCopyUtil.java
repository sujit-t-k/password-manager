package org.ajikhoji.passwordmanager.util;

import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClipboardCopyUtil {

    private final static Clipboard cb = Clipboard.getSystemClipboard();
    public static void copyText(final String str) {
        final Map<DataFormat, Object> copyMap = new HashMap<>();
        copyMap.put(DataFormat.PLAIN_TEXT, str);
        cb.setContent(copyMap);
    }

    public enum ContentType {
        ACCOUNT_ID,
        PASSWORD,
        GENERAL;
    }


    private final static Set<Long> accNameCopied = new HashSet<>();
    private final static Set<Long> accPassCopied = new HashSet<>();

    public static void copyText(final String content, final ContentType ct, final long accId, final Runnable onAccountInfoCopy) {
        copyText(content);
        if(ct.equals(ContentType.ACCOUNT_ID)) {
            accNameCopied.add(accId);
        } else if (ct.equals(ContentType.PASSWORD)) {
            accPassCopied.add(accId);
        }
        if(accNameCopied.contains(accId) && accPassCopied.contains(accId)) {
            onAccountInfoCopy.run();
            accNameCopied.remove(accId);
            accPassCopied.remove(accId);
        }
    }

    public static void clear() {
        accPassCopied.clear();
        accNameCopied.clear();
    }

}
