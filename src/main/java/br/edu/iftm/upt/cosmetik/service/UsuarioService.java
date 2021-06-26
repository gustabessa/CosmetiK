package br.edu.iftm.upt.cosmetik.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.iftm.upt.cosmetik.model.Usuario;
import br.edu.iftm.upt.cosmetik.repository.usuario.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Transactional
	public void salvar(Usuario usuario) {
		usuarioRepository.save(usuario);
	}
}
