package br.edu.iftm.upt.cosmetik.service;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@Service
public class RelatorioService {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioService.class);

    @Autowired
    private DataSource dataSource;
    
    public byte[] gerarRelatorioDeVendas() {
        
        try (Connection conexao = dataSource.getConnection()) {
                try {
                    ClassPathResource cpr = new ClassPathResource("reports/relatorio_vendas.jasper");
                    InputStream arquivoJasper = cpr.getInputStream();

                    String urlRelatorio = cpr.getURL().toString();
                    String diretorioRelatorios = urlRelatorio.substring(0,
                            urlRelatorio.lastIndexOf(File.separator) + 1);

                    Map<String, Object> parametros = new HashMap<>();
                    parametros.put("SUBREPORT_DIR", diretorioRelatorios);

                    JasperPrint jasperPrint = JasperFillManager.fillReport(arquivoJasper, parametros, conexao);
                    return JasperExportManager.exportReportToPdf(jasperPrint);
                } catch (java.io.IOException e) {
                    logger.error("Erro encontrando o caminho do subreport: " + e);
                } catch (JRException e) {
                    logger.error("Problemas na geracao do PDF do relatório: " + e);
                }
            } catch (SQLException e) {
                logger.error("Problemas na obtenção de uma conexão com o BD na geração de relatório: " + e);
            }
            return null;
    }

}