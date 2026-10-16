package br.com.zenon.fraud.detector;

import br.com.zenon.fraud.detector.service.TransactionReport;

import java.io.IOException;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public final class ReportMain {
    private static final Path DEFAULT_INPUT_PATH = Path.of("data/PS_20174392719_1491204439457_log.csv");
    private static final Locale PORTUGUESE_LOCALE = Locale.of("pt", "BR");
    private static final Locale ENGLISH_LOCALE = Locale.US;

    private ReportMain() {
    }

    public static void main(String[] args) throws IOException {
        CommandLine commandLine = parseArguments(args);
        TransactionReport.Result result = new TransactionReport().generate(commandLine.inputPath());
        ResourceBundle labels = ResourceBundle.getBundle("report", commandLine.locale());
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(commandLine.locale());

        System.out.println(labels.getString("totalLines") + ": " + result.totalLines());
        System.out.println(labels.getString("totalFrauds") + ": " + result.totalFrauds());
        System.out.println(labels.getString("totalValue") + ": " + currencyFormatter.format(result.totalAmount()));
    }

    private static CommandLine parseArguments(String[] args) {
        if (args.length > 2) {
            throw new IllegalArgumentException("Informe --lang=pt ou --lang=en e no máximo um caminho de arquivo CSV");
        }

        Locale locale = PORTUGUESE_LOCALE;
        Path inputPath = DEFAULT_INPUT_PATH;
        boolean languageSpecified = false;
        boolean inputPathSpecified = false;

        for (String argument : args) {
            if (argument.startsWith("--lang=")) {
                if (languageSpecified) {
                    throw new IllegalArgumentException("Informe apenas um idioma com --lang=pt ou --lang=en");
                }
                locale = localeFor(argument.substring("--lang=".length()));
                languageSpecified = true;
            } else if (argument.equals("--lang")) {
                throw new IllegalArgumentException("Informe o idioma com --lang=pt ou --lang=en");
            } else if (inputPathSpecified) {
                throw new IllegalArgumentException("Informe no máximo um caminho de arquivo CSV");
            } else {
                inputPath = Path.of(argument);
                inputPathSpecified = true;
            }
        }

        return new CommandLine(locale, inputPath);
    }

    private static Locale localeFor(String language) {
        return switch (language) {
            case "pt" -> PORTUGUESE_LOCALE;
            case "en" -> ENGLISH_LOCALE;
            default -> throw new IllegalArgumentException("Idioma inválido: " + language + ". Use --lang=pt ou --lang=en");
        };
    }

    private record CommandLine(Locale locale, Path inputPath) {
    }
}
