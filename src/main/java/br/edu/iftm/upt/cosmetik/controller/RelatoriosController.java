package br.edu.iftm.upt.cosmetik.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.edu.iftm.upt.cosmetik.service.RelatorioService;

@Controller
@RequestMapping("/relatorios")
public class RelatoriosController {

    @Autowired
    private RelatorioService relatorioService;
    
    @GetMapping("/relatorioDeVendas")
    public ResponseEntity<byte[]> gerarRelatorioDeVendas() {
        
        byte[] relatorio = relatorioService.gerarRelatorioDeVendas();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Vendas.pdf")
                .body(relatorio);
    }

}
