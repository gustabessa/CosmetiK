package br.edu.iftm.upt.cosmetik.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.iftm.upt.cosmetik.model.ItemVenda;
import br.edu.iftm.upt.cosmetik.repository.itemVenda.ItemVendaRepository;

@Service
public class ItemVendaService {

	@Autowired
	private ItemVendaRepository itemVendaRepository;

	public List<ItemVenda> salvarLista(List<ItemVenda> itens) {
		return itemVendaRepository.saveAll(itens);
	}
	
}
