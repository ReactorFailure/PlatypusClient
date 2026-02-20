package net.reactorfailure.platypusclient.qol.TooltipTextWrap;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TooltipTextWrap {
    private static final int MAX_TOOLTIP_WIDTH = 200; // Maximum width in pixels before wrapping

    public static List<Text> wrapTooltipLines(List<Text> lines, TextRenderer textRenderer) {
        List<Text> wrappedLines = new ArrayList<>();

        for (Text line : lines) {
            int width = textRenderer.getWidth(line);

            if (width <= MAX_TOOLTIP_WIDTH) {
                wrappedLines.add(line);
            } else {
                wrappedLines.addAll(wrapLine(line, textRenderer, MAX_TOOLTIP_WIDTH));
            }
        }

        return wrappedLines;
    }


    private static List<Text> wrapLine(Text text, TextRenderer textRenderer, int maxWidth) {
        List<Text> wrappedLines = new ArrayList<>();

        List<OrderedText> orderedLines = textRenderer.wrapLines(text, maxWidth);

        for (OrderedText orderedText : orderedLines) {
            wrappedLines.add(Text.literal(orderedTextToString(orderedText)));
        }

        return wrappedLines;
    }


    private static String orderedTextToString(OrderedText orderedText) {
        StringBuilder builder = new StringBuilder();

        orderedText.accept((index, style, codePoint) -> {
            builder.appendCodePoint(codePoint);
            return true;
        });

        return builder.toString();
    }
}
