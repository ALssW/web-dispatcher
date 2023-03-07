package com.alva.dispatcher.caster;

import com.alva.dispatcher.exception.CasterException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-14
 */
public class MultipartCaster implements BaseCaster {

	@Override
	public Object cast(HttpServletRequest request, Parameter parameter) throws CasterException {
		List<Part> partList;
		try {
			Collection<Part> parts = request.getParts();
			partList = parts.stream().filter(part -> part.getContentType() != null && part.getContentType().startsWith("image")).
					collect(Collectors.toList());

			if (partList.size() == 1) {
				return partList.get(0);
			}

		} catch (IOException | ServletException e) {
			throw new CasterException(e);
		}
		return partList;
	}

}
