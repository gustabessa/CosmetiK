package br.edu.iftm.upt.cosmetik.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.iftm.upt.cosmetik.model.Venda;
import br.edu.iftm.upt.cosmetik.repository.venda.VendaRepository;

@Service
public class VendaService {

	@Autowired
	private VendaRepository vendaRepository;

	public Venda salvar(Venda venda) {
		return vendaRepository.save(venda);
	}
	
}
