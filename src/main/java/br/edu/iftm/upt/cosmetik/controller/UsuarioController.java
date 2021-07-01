package br.edu.iftm.upt.cosmetik.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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
	public ModelAndView cadastrarNovoUsuario(Usuario usuario) {
		ModelAndView mv;
		Boolean jaExistente = true;
		if (usuario.getNomeUsuario() != null && !usuario.getNomeUsuario().trim().isEmpty()) {			
			jaExistente = usuarioService.usuarioJaExistente(usuario.getNomeUsuario());
		}
		if (!usuario.getPapeis().isEmpty() && !jaExistente) {
			usuario.setAtivo(true);
			usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
			usuarioService.salvar(usuario);
			mv = new ModelAndView("redirect:/usuario/novo");
		} else {
			List<Papel> papeis = papelRepository.findAll();
			mv = new ModelAndView("usuario/cadastrousuario");
			mv.addObject("todosPapeis", papeis);
		}
		return mv;
	}
}
