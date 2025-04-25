package com.vitalu.flop.service;

import java.io.IOException;
import java.util.Base64;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.vitalu.flop.exception.FlopException;

@Service
public class ImagemService {

	public String processarImagem(MultipartFile file) throws FlopException {
		byte[] imagemBytes;
		try {
			imagemBytes = file.getBytes();
		} catch (IOException e) {
			throw new FlopException("Houve um erro ao processar o arquivo.",
					HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE);
		}

		String base64Imagem = Base64.getEncoder().encodeToString(imagemBytes);

		return base64Imagem;
	}
}