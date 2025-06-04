package com.vitalu.flop.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.vitalu.flop.exception.FlopException;
import com.vitalu.flop.model.dto.AvaliacaoDTO;
import com.vitalu.flop.model.entity.Avaliacao;
import com.vitalu.flop.model.entity.Praia;
import com.vitalu.flop.model.entity.Usuario;
import com.vitalu.flop.model.enums.Condicoes;
import com.vitalu.flop.model.repository.AvaliacaoRepository;
import com.vitalu.flop.model.repository.PraiaRepository;
import com.vitalu.flop.model.repository.UsuarioRepository;
import com.vitalu.flop.model.seletor.AvaliacaoSeletor;

@Service
public class AvaliacaoService {

	@Autowired
	private AvaliacaoRepository avaliacaoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PraiaRepository praiaRepository;

	public AvaliacaoDTO cadastrar(AvaliacaoDTO avaliacaoDTO) throws FlopException {

		if (avaliacaoDTO == null) {
			throw new FlopException("Dados da avaliação inválidos.", HttpStatus.BAD_REQUEST);
		}

		Usuario usuario = usuarioRepository.findById(avaliacaoDTO.getIdUsuario())
				.orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.BAD_REQUEST));

		Praia praia = praiaRepository.findById(avaliacaoDTO.getIdPraia())
				.orElseThrow(() -> new FlopException("Praia não encontrada.", HttpStatus.NOT_FOUND));

		// Verificar se o usuário já fez uma avaliação hoje NESTA praia específica
		verificarAvaliacaoHojeNaPraia(usuario.getIdUsuario(), praia.getIdPraia());

		Avaliacao avaliacao = new Avaliacao();
		avaliacao.setUsuario(usuario);
		avaliacao.setPraia(praia);
		avaliacao.setCondicoes(avaliacaoDTO.getCondicoes());

		validarCondicoes(avaliacaoDTO.getCondicoes());
		Avaliacao avaliacaoSalva = avaliacaoRepository.save(avaliacao);
		return Avaliacao.toDTO(avaliacaoSalva);
	}

	public AvaliacaoDTO buscarPorId(Long idAvaliacao) throws FlopException {
		Avaliacao avaliacao = avaliacaoRepository.findById(idAvaliacao)
				.orElseThrow(() -> new FlopException("A avaliação buscada não foi encontrada.", HttpStatus.NOT_FOUND));

		AvaliacaoDTO dto = Avaliacao.toDTO(avaliacao);
		return dto;
	}

	public AvaliacaoDTO atualizar(Long idAvaliacao, AvaliacaoDTO editarAvaliacaoDTO) throws FlopException {

		Avaliacao avaliacaoExistente = avaliacaoRepository.findById(idAvaliacao)
				.orElseThrow(() -> new FlopException("Avaliação não encontrada.", HttpStatus.NOT_FOUND));

		// Verificar se a avaliação foi criada hoje (permitir edição apenas no mesmo
		// dia)
		verificarEdicaoPermitida(avaliacaoExistente);

		if (editarAvaliacaoDTO.getCondicoes() != null && !editarAvaliacaoDTO.getCondicoes().isEmpty()) {
			validarCondicoes(editarAvaliacaoDTO.getCondicoes());
			avaliacaoExistente.setCondicoes(editarAvaliacaoDTO.getCondicoes());
		}

		Avaliacao avaliacaoAtualizada = avaliacaoRepository.save(avaliacaoExistente);
		return Avaliacao.toDTO(avaliacaoAtualizada);
	}

	public void excluir(Long idAvaliacao, Long idUsuario) throws FlopException {
		Avaliacao avaliacao = avaliacaoRepository.findById(idAvaliacao).orElseThrow(
				() -> new FlopException("A avaliação selecionada não foi encontrada.", HttpStatus.BAD_REQUEST));
		Usuario usuario = usuarioRepository.findById(idUsuario)
				.orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.NOT_FOUND));

		boolean isAdmin = usuario.isAdmin();
		boolean isOwner = avaliacao.getUsuario().getIdUsuario().equals(usuario.getIdUsuario());

		if (!isAdmin && !isOwner) {
			throw new FlopException("Você não é o Dono desta avaliação, portanto não pode excluí-la.",
					HttpStatus.FORBIDDEN);
		}
		avaliacaoRepository.delete(avaliacao);
	}

//	  Verifica se o usuário já fez uma avaliação hoje NESTA praia específica

	private void verificarAvaliacaoHojeNaPraia(Long idUsuario, Long idPraia) throws FlopException {
		LocalDate hoje = LocalDate.now();
		LocalDateTime inicioHoje = hoje.atStartOfDay();
		LocalDateTime fimHoje = hoje.atTime(LocalTime.MAX);

		// Busca avaliações do usuário criadas hoje NESTA praia específica
		Optional<Avaliacao> avaliacaoHojeNaPraia = avaliacaoRepository
				.findByUsuarioIdUsuarioAndPraiaIdPraiaAndCriadoEmBetween(idUsuario, idPraia, inicioHoje, fimHoje);

		if (avaliacaoHojeNaPraia.isPresent()) {
			String nomePraia = avaliacaoHojeNaPraia.get().getPraia().getNomePraia();
			throw new FlopException("Você já fez uma avaliação hoje para a praia " + nomePraia + ". "
					+ "Você pode editar sua avaliação existente, mas não pode criar uma nova para esta praia hoje.",
					HttpStatus.BAD_REQUEST);
		}
	}

//	  Verifica se a avaliação pode ser editada (apenas no mesmo dia da criação)

	private void verificarEdicaoPermitida(Avaliacao avaliacao) throws FlopException {
		LocalDate hoje = LocalDate.now();
		LocalDate dataAvaliacao = avaliacao.getCriadoEm().toLocalDate();

		if (!dataAvaliacao.equals(hoje)) {
			throw new FlopException("Você só pode editar avaliações criadas no mesmo dia. "
					+ "Esta avaliação foi criada em " + dataAvaliacao + " e não pode mais ser editada.",
					HttpStatus.FORBIDDEN);
		}
	}

//	 Busca a avaliação do usuário feita hoje para uma praia específica (se existir)

	public AvaliacaoDTO buscarAvaliacaoDoUsuarioHojeNaPraia(Long idUsuario, Long idPraia) throws FlopException {
		Usuario usuario = usuarioRepository.findById(idUsuario)
				.orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.NOT_FOUND));
		Praia praia = praiaRepository.findById(idPraia)
				.orElseThrow(() -> new FlopException("Praia não encontrada.", HttpStatus.NOT_FOUND));

		LocalDate hoje = LocalDate.now();
		LocalDateTime inicioHoje = hoje.atStartOfDay();
		LocalDateTime fimHoje = hoje.atTime(LocalTime.MAX);

		Optional<Avaliacao> avaliacaoHojeNaPraia = avaliacaoRepository
				.findByUsuarioIdUsuarioAndPraiaIdPraiaAndCriadoEmBetween(idUsuario, idPraia, inicioHoje, fimHoje);

		if (avaliacaoHojeNaPraia.isEmpty()) {
			throw new FlopException("Nenhuma avaliação encontrada para hoje na praia " + praia.getNomePraia() + ".",
					HttpStatus.NOT_FOUND);
		}

		return Avaliacao.toDTO(avaliacaoHojeNaPraia.get());
	}

//	  Busca todas as avaliações do usuário feitas hoje (em todas as praias)

	public List<AvaliacaoDTO> buscarAvaliacoesDoUsuarioHoje(Long idUsuario) throws FlopException {
		Usuario usuario = usuarioRepository.findById(idUsuario)
				.orElseThrow(() -> new FlopException("Usuário não encontrado.", HttpStatus.NOT_FOUND));

		LocalDate hoje = LocalDate.now();
		LocalDateTime inicioHoje = hoje.atStartOfDay();
		LocalDateTime fimHoje = hoje.atTime(LocalTime.MAX);

		Optional<Avaliacao> avaliacoesHoje = avaliacaoRepository.findByUsuarioIdUsuarioAndCriadoEmBetween(idUsuario,
				inicioHoje, fimHoje);

		return avaliacoesHoje.stream().map(Avaliacao::toDTO).toList();
	}

	private void validarCondicoes(List<Condicoes> condicoes) throws FlopException {
		Map<Condicoes, List<Condicoes>> condicoesIncompativeis = Map.of(Condicoes.SOL,
				List.of(Condicoes.CHUVA, Condicoes.NUBLADO), Condicoes.CHUVA, List.of(Condicoes.SOL), Condicoes.NUBLADO,
				List.of(Condicoes.SOL), Condicoes.MAR_CALMO, List.of(Condicoes.MAR_ONDAS), Condicoes.MAR_ONDAS,
				List.of(Condicoes.MAR_CALMO), Condicoes.LIMPA, List.of(Condicoes.LIXO), Condicoes.LIXO,
				List.of(Condicoes.LIMPA)

		);

		// Verifica se alguma condição selecionada entra em conflito com outra
		for (Condicoes condicao : condicoes) {
			List<Condicoes> conflitantes = condicoesIncompativeis.get(condicao);
			if (conflitantes != null && condicoes.stream().anyMatch(conflitantes::contains)) {
				throw new FlopException("As condições selecionadas são incompatíveis: " + condicao
						+ " não pode estar junto com " + conflitantes.stream().filter(condicoes::contains).toList(),
						HttpStatus.BAD_REQUEST);
			}
		}
	}

	public Page<AvaliacaoDTO> pesquisarComFiltros(AvaliacaoSeletor seletor) throws FlopException {
		Pageable pageable = Pageable.unpaged();

		if (seletor.temPaginacao()) {
			pageable = PageRequest.of(seletor.getPagina() - 1, seletor.getLimite(), Sort.by("criadoEm").descending());
		}

		Page<Avaliacao> avaliacoesFiltradas = avaliacaoRepository.findAll(seletor, pageable);

		return avaliacoesFiltradas.map(Avaliacao::toDTO);
	}

}