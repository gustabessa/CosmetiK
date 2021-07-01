package br.edu.iftm.upt.cosmetik.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import br.edu.iftm.upt.cosmetik.model.ItemVenda;
import br.edu.iftm.upt.cosmetik.model.Usuario;
import br.edu.iftm.upt.cosmetik.service.UsuarioService;

@Controller
public class IndexController {

	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
	
	@Autowired
	private UsuarioService usuarioService;

	@GetMapping(value = {"/", "/index.html"} )
	public ModelAndView index() {
		logger.trace("Entrou em index");
		ModelAndView mv = new ModelAndView("index");
		//Se voce precisar pode inserir outros objetos no model para que sejam usados
		// na view index.html
		//mv.addObject("nome", valor);
		logger.trace("Encaminhando para a view index");
		return mv;
	}
	
	@GetMapping("/mostrarmensagem")
	public ModelAndView mostrarMensagem(String mensagem) {
		ModelAndView mv = new ModelAndView("mostrarmensagem");
		mv.addObject("mensagem", mensagem);
		return mv;
	}
	
	@GetMapping("/sessionauthenticate")
	public ModelAndView sessionAuthenticate(HttpSession sessao) {
		String nomeUsuario = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
		Usuario usuario = usuarioService.buscarPorNomeUsuario(nomeUsuario);
		usuario.setSenha(null);
		sessao.setAttribute("itens", new ArrayList<ItemVenda>());
		sessao.setAttribute("usuario", usuario);
		return new ModelAndView("redirect:/");
	}
	
}