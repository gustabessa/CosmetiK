package br.edu.iftm.upt.cosmetik.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.edu.iftm.upt.cosmetik.filter.ProdutoFilter;
import br.edu.iftm.upt.cosmetik.model.Produto;
import br.edu.iftm.upt.cosmetik.repository.produto.ProdutoRepository;

@Service
public class ProdutoService {
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Transactional
	public Produto salvar(Produto produto) {
		return produtoRepository.save(produto);
	}

	public Page<Produto> pesquisar(ProdutoFilter filtro, Pageable paginado) {
		String nomeF = filtro.getNomeF();
		if (nomeF != null) {
			nomeF = "%".concat(nomeF.toUpperCase()).concat("%");
		}
		return produtoRepository.pesquisar(nomeF, paginado);
	}

	public Produto buscarPorId(Long idProduto) {
		Optional<Produto> produto = produtoRepository.findById(idProduto);
		if (produto.isPresent()) {
			return produto.get();
		}
		return null;
	}

}
