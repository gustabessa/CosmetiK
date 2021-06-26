package br.edu.iftm.upt.cosmetik.repository.produto;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.edu.iftm.upt.cosmetik.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

	@Query("SELECT P FROM Produto P")
	Page<Produto> pesquisar(Pageable paginado);
	
}
