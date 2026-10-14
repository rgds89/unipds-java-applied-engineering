package br.com.zenon.fraud.detector;

import br.com.zenon.fraud.detector.service.TransactionReport;

import java.io.IOException;
import java.nio.file.Path;

public final class ReportMain {
    private static final Path DEFAULT_INPUT_PATH = Path.of("data/PS_20174392719_1491204439457_log.csv");

    private ReportMain() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            throw new IllegalArgumentException("Informe no máximo um caminho de arquivo CSV");
        }

        Path inputPath = args.length == 1 ? Path.of(args[0]) : DEFAULT_INPUT_PATH;
        TransactionReport.Result result = new TransactionReport().generate(inputPath);

        System.out.println("Total de linhas: " + result.totalLines());
        System.out.println("Total de fraudes: " + result.totalFrauds());
        System.out.println("Valor total transacionado: " + result.totalAmount().toPlainString());
    }
}
