package br.edu.iftm.upt.cosmetik.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

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
import br.edu.iftm.upt.cosmetik.model.ItemVenda;
import br.edu.iftm.upt.cosmetik.model.Produto;
import br.edu.iftm.upt.cosmetik.model.Usuario;
import br.edu.iftm.upt.cosmetik.model.Venda;
import br.edu.iftm.upt.cosmetik.pagination.PageWrapper;
import br.edu.iftm.upt.cosmetik.service.ItemVendaService;
import br.edu.iftm.upt.cosmetik.service.ProdutoService;
import br.edu.iftm.upt.cosmetik.service.VendaService;

@Controller
@RequestMapping("/venda")
@SuppressWarnings("unchecked")
public class VendaController {

	@Autowired
	private VendaService vendaService;
	
	@Autowired
	private ProdutoService produtoService;
	
	@Autowired
	private ItemVendaService itemVendaService;
	
	@GetMapping("/produtos")
	public ModelAndView pesquisarProdutos(ProdutoFilter filtro, @PageableDefault(size = 5)
		    @SortDefault(sort = "nome", direction = Sort.Direction.ASC)
		    Pageable paginado, HttpServletRequest request) {
		
		Page<Produto> pagina = produtoService.pesquisar(filtro, paginado);
		
		PageWrapper<Produto> paginaWrapper = new PageWrapper<>(pagina, request);
		
		ModelAndView mv = new ModelAndView("venda/mostrarproduto");
		mv.addObject("pagina", paginaWrapper);
		mv.addObject("filtro", filtro);
		
		return mv;
	}
	
	@GetMapping("/selecionarproduto")
	public ModelAndView encaminharSelecaoProduto(Long idProduto) {
		ModelAndView mv = new ModelAndView();
		if (idProduto != null) {
			Produto produto = produtoService.buscarPorId(idProduto);
			mv.setViewName("venda/selecionarproduto");
			mv.addObject(produto);
		} else {
			mv.setViewName("mostrarmensagem");
			mv.addObject("mensagem", "Use um valor válido de codigo.");
		}
		return mv;
	}
	
	@PostMapping("/selecionarproduto")
	public ModelAndView selecionarProduto(Produto produto, RedirectAttributes atributos, HttpSession sessao) {
		ModelAndView mv = new ModelAndView();
		Integer quantidade = produto.getQuantidade();
		if (quantidade == null || quantidade == 0) {
			mv.addObject(produto);
			mv.setViewName("venda/selecionarproduto");
		} else {
			atributos.addFlashAttribute("filtro", new ProdutoFilter());
			List<ItemVenda> itens = (List<ItemVenda>) sessao.getAttribute("itens");
			if (itens == null) {
				itens = new ArrayList<>();
			}
			itens.add(new ItemVenda(itens.size() + 1, quantidade, produto, produto.getPreco(),
					produto.getPreco().multiply(new BigDecimal(quantidade))));
			sessao.setAttribute("itens", itens);
			mv.setViewName("redirect:/venda/produtos");
		}
		return mv;
	}
	
	@GetMapping("/itensvenda")
	public ModelAndView exibirItensVenda(HttpSession sessao) {
		ModelAndView mv = new ModelAndView();
		List<ItemVenda> itens = (List<ItemVenda>) sessao.getAttribute("itens");
		if (itens == null) {
			itens = new ArrayList<ItemVenda>();
		}
		// Somar todos os valores totais dos itens para obter o valor total da venda
		BigDecimal vlrTotal = itens.stream().map(item -> item.getVlrTotal()).reduce(BigDecimal.ZERO, BigDecimal::add);
		
		mv.addObject("itens", itens);
		mv.addObject("vlrTotal", vlrTotal);
		mv.setViewName("/venda/itensvenda");
		
		return mv;
	}
	
	@GetMapping("/removeritem")
	public ModelAndView removerItemVenda(Integer numItem, HttpSession sessao) {
		List<ItemVenda> itens = (List<ItemVenda>) sessao.getAttribute("itens");
		// Filtrar lista de itens da sessão, removendo o item que tenha o numItem passado, e atualiza a lista na sessão
		itens = itens.stream().filter(item -> !item.getNumItem().equals(numItem)).collect(Collectors.toList());
		// Ajustar numItem (decrementar posteriores ao removido)
		for (ItemVenda item : itens) {
			if (item.getNumItem().compareTo(numItem) > 0) {
				item.decrementarNumItem();
			}
		}
		
		sessao.setAttribute("itens", itens);
		// Redireciona para a tela de carrinho
		return new ModelAndView("redirect:/venda/itensvenda");
	}
	
	@Transactional
	@PostMapping("/finalizar")
	public ModelAndView finalizarVenda(HttpSession sessao, RedirectAttributes atributos) {
		ModelAndView mv = new ModelAndView("redirect:/mostrarmensagem");
		atributos.addAttribute("mensagem", "Venda realizada com sucesso!");
		Usuario usuario = (Usuario) sessao.getAttribute("usuario");
		List<ItemVenda> itens = (List<ItemVenda>) sessao.getAttribute("itens");
		BigDecimal vlrTotal = itens.stream().map(item -> item.getVlrTotal()).reduce(BigDecimal.ZERO, BigDecimal::add);
		
		Venda venda = new Venda(vlrTotal, usuario, LocalDateTime.now());
		for (ItemVenda item : itens) {
			item.setVenda(venda);
		}
		
		vendaService.salvar(venda);
		itemVendaService.salvarLista(itens);
		
		return mv;
	}
}
