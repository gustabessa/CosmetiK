package br.edu.iftm.upt.cosmetik.controller;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import br.edu.iftm.upt.cosmetik.filter.ProdutoFilter;
import br.edu.iftm.upt.cosmetik.model.Produto;
import br.edu.iftm.upt.cosmetik.pagination.PageWrapper;
import br.edu.iftm.upt.cosmetik.service.ProdutoService;

@Controller
@RequestMapping("/produto")
public class ProdutoController {
	
	@Autowired
	private ProdutoService produtoService;
	
	@GetMapping("/novo")
	public ModelAndView direcionarCadastroProduto(Produto produto) {
		ModelAndView mv = new ModelAndView("produto/cadastroproduto");
		return mv;
	}
	
	@PostMapping("/novo")
	public ModelAndView cadastrarNovoUsuario(Produto produto) {
		BigDecimal preco = produto.getPreco();
		if (produto.getNome().trim() != "" && preco != null) {
			produtoService.salvar(produto);
			return new ModelAndView("redirect:/produto/novo");
		}
		return new ModelAndView("produto/cadastroproduto");
	}
	
	@GetMapping("/pesquisar")
	public ModelAndView pesquisar(ProdutoFilter filtro, @PageableDefault(size = 5)
	                                                    @SortDefault(sort = "nome", direction = Sort.Direction.ASC)
	                                                    Pageable paginado, HttpServletRequest request) {
		Page<Produto> pagina = produtoService.pesquisar(filtro, paginado);
		
		PageWrapper<Produto> paginaWrapper = new PageWrapper<>(pagina, request);

		ModelAndView mv = new ModelAndView("produto/mostrarproduto");
		mv.addObject("pagina", paginaWrapper);

		return mv;
	}
	
	@PostMapping("/buscar")
	public ModelAndView buscarContatoPeloId(Long idProduto) {
		ModelAndView mv = new ModelAndView();
		if (idProduto != null) {
			Produto produto = produtoService.buscarPorId(idProduto);
			
			//Se tiver um objeto
			if (produto != null) {
				mv.setViewName("produto/editarproduto");
				mv.addObject("produto", produto);
			} else {
				mv.setViewName("mostrarmensagem");
				mv.addObject("mensagem", "Nenhum contato encontrado com o id " + idProduto);
			}
		} else {
			mv.setViewName("mostrarmensagem");
			mv.addObject("mensagem", "Use um valor válido de codigo.");
		}
		return mv;
	}
	
}
