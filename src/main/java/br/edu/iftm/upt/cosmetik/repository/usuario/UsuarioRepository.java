package br.edu.iftm.upt.cosmetik.repository.usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.iftm.upt.cosmetik.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Long countByNomeUsuario(String nomeUsuario);

	Usuario findByNomeUsuario(String nomeUsuario);

}
