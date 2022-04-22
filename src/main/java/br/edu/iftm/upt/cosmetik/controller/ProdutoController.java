package br.edu.iftm.upt.cosmetik.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	public ModelAndView cadastrarNovoProduto(Produto produto, RedirectAttributes atributos) {
		ModelAndView mv = new ModelAndView();
		List<String> mensagens = new ArrayList<String>();
		BigDecimal preco = produto.getPreco();
		if (!produto.getNome().trim().isEmpty() && preco != null) {
			produto.setAtivo(true);
			produtoService.salvar(produto);
			atributos.addFlashAttribute("mensagemSucesso", "Produto cadastrado com sucesso!");
			return new ModelAndView("redirect:/produto/novo");
		}
		if (produto.getNome().trim().isEmpty()) {
			mensagens.add("Por favor, preencha o nome do produto.");
		}
		if (produto.getPreco() == null || produto.getPreco().equals(BigDecimal.ZERO)) {
			mensagens.add("Por favor, preencha o preço do produto.");
		}
		mv.setViewName("produto/cadastroproduto");
		mv.addObject("mensagens", mensagens);
		return mv;
	}
	
	@GetMapping("/pesquisar")
	public ModelAndView pesquisar(ProdutoFilter filtro, @PageableDefault(size = 5)
	                                                    @SortDefault(sort = "nome", direction = Sort.Direction.ASC)
	                                                    Pageable paginado, HttpServletRequest request) {
		Page<Produto> pagina = produtoService.pesquisar(filtro, paginado);
		
		PageWrapper<Produto> paginaWrapper = new PageWrapper<>(pagina, request);

		ModelAndView mv = new ModelAndView("produto/mostrarproduto");
		mv.addObject("pagina", paginaWrapper);
		mv.addObject("filtro", filtro);

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
	
	@PostMapping("/editar") 
	public ModelAndView editar(Produto produto) {
		BigDecimal preco = produto.getPreco();
		if (!produto.getNome().trim().equals("") && preco != null) {
			produtoService.salvar(produto);
			return new ModelAndView("redirect:/produto/pesquisar");
		}
		return new ModelAndView("produto/editarproduto");
	}
	
	@PostMapping("/remover") 
	public ModelAndView remover(Produto produto) {
		ModelAndView mv;
		if (produto.getCodigo() != null) {
			produto = produtoService.buscarPorId(produto.getCodigo());
			produto.setAtivo(false);
			produtoService.salvar(produto);
			return new ModelAndView("redirect:/produto/pesquisar");
		}
		mv = new ModelAndView();
		mv.setViewName("mostrarmensagem");
		mv.addObject("mensagem", "Use um valor válido de codigo.");
		return mv;
	}
	
	@PostMapping("/confirmarremocao")
	public ModelAndView direcionarConfirmarRemocao(Produto produto) {
		return new ModelAndView("produto/confirmarremocao");
	}
}
