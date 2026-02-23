package net.reactorfailure.platypusclient.qol.TooltipTextWrap;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TooltipTextWrap {
    private static final int MAX_TOOLTIP_WIDTH = 200;


    public static List<Text> wrapTooltipLines(List<Text> lines, TextRenderer textRenderer) {
        List<Text> wrappedLines = new ArrayList<>();

        for (Text line : lines) {
            if (textRenderer.getWidth(line) <= MAX_TOOLTIP_WIDTH) {
                wrappedLines.add(line);
            } else {
                List<OrderedText> orderedLines = textRenderer.wrapLines(line, MAX_TOOLTIP_WIDTH);
                for (OrderedText ordered : orderedLines) {
                    StringBuilder sb = new StringBuilder();
                    ordered.accept((index, style, codePoint) -> {
                        sb.appendCodePoint(codePoint);
                        return true;
                    });
                    wrappedLines.add(Text.literal(sb.toString()));
                }
            }
        }

        return wrappedLines;
    }
}
