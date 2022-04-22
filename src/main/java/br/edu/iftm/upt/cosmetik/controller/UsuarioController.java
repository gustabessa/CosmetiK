package br.edu.iftm.upt.cosmetik.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.iftm.upt.cosmetik.model.Papel;
import br.edu.iftm.upt.cosmetik.model.Usuario;
import br.edu.iftm.upt.cosmetik.repository.papel.PapelRepository;
import br.edu.iftm.upt.cosmetik.service.UsuarioService;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {
	
	@Autowired
	private PapelRepository papelRepository;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/novo")
	public ModelAndView direcionarCadastroUsuario(Usuario usuario) {
		ModelAndView mv = new ModelAndView("usuario/cadastrousuario");
		List<Papel> papeis = papelRepository.findAll();
		mv.addObject("todosPapeis", papeis);
		return mv;
	}

	@PostMapping("/novo")
	public ModelAndView cadastrarNovoUsuario(Usuario usuario, RedirectAttributes atributos) {
		ModelAndView mv;
		List<String> mensagens = new ArrayList<String>();
		String mensagemSucesso = null;
		
		if (validarNovoUsuario(mensagens, usuario)) {
			usuario.setAtivo(true);
			usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
			usuarioService.salvar(usuario);
			mensagemSucesso = "Usuário cadastrado com sucesso.";
			atributos.addFlashAttribute("mensagemSucesso", mensagemSucesso);
			mv = new ModelAndView("redirect:/usuario/novo");
		} else {
			List<Papel> papeis = papelRepository.findAll();
			mv = new ModelAndView("usuario/cadastrousuario");
			mv.addObject("todosPapeis", papeis);
		}
		
		mv.addObject("mensagens", mensagens);
		return mv;
	}
	
	private Boolean validarNovoUsuario(List<String> mensagens, Usuario usuario) {
		Boolean valido = true;
		Boolean jaExistente = true;
		
		if (usuario.getNomeUsuario() != null && !usuario.getNomeUsuario().trim().isEmpty()) {			
			jaExistente = usuarioService.usuarioJaExistente(usuario.getNomeUsuario());
		} else {
			mensagens.add("Preencha o nome de usuário.");
			valido = false;
		}
		
		if (usuario.getPapeis().isEmpty()) {			
			mensagens.add("Selecione ao menos um papel.");
			valido = false;
		}
		
		if (jaExistente) {
			mensagens.add("Nome de usuário já existe.");
			valido = false;			
		}
		
		if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
			mensagens.add("Preencha corretamente o nome.");
			valido = false;
		}
		
		if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
			mensagens.add("Preencha corretamente a senha.");
			valido = false;
		}
		
		if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
			mensagens.add("Preencha corretamente o email.");
			valido = false;
		}
		
		return valido;
	}
}
