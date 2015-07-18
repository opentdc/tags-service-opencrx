/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Arbalo AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.opentdc.tags.opencrx;

import java.util.List;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.servlet.ServletContext;

import org.openmdx.base.exception.ServiceException;
import org.opentdc.opencrx.AbstractOpencrxServiceProvider;
import org.opentdc.service.LocalizedTextModel;
import org.opentdc.service.exception.DuplicateException;
import org.opentdc.service.exception.InternalServerErrorException;
import org.opentdc.service.exception.NotFoundException;
import org.opentdc.service.exception.ValidationException;
import org.opentdc.tags.ServiceProvider;
import org.opentdc.tags.TagTextModel;
import org.opentdc.tags.TagsModel;

/**
 * Tags service for openCRX.
 *
 */
public class OpencrxServiceProvider extends AbstractOpencrxServiceProvider implements ServiceProvider {
	
	private static final Logger logger = Logger.getLogger(OpencrxServiceProvider.class.getName());

	/**
	 * Constructor.
	 * 
	 * @param context the servlet context
	 * @param prefix the simple class name of the service provider
	 * @throws ServiceException
	 * @throws NamingException
	 */
	public OpencrxServiceProvider(
		ServletContext context, 
		String prefix
	) throws ServiceException, NamingException {
		super(context, prefix);
	}

	/* (non-Javadoc)
	 * @see org.opentdc.tags.ServiceProvider#list(java.lang.String, java.lang.String, long, long)
	 */
	@Override
	public List<TagTextModel> list(
		String queryType,
		String query,
		int position,
		int size
	) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.opentdc.tags.ServiceProvider#create(org.opentdc.tags.TagsModel)
	 */
	@Override
	public TagsModel create(
		TagsModel tag
	) throws DuplicateException, ValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.opentdc.tags.ServiceProvider#read(java.lang.String)
	 */
	@Override
	public TagsModel read(
		String id
	) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.opentdc.tags.ServiceProvider#update(java.lang.String, org.opentdc.tags.TagsModel)
	 */
	@Override
	public TagsModel update(
		String id, 
		TagsModel tag
	) throws NotFoundException, ValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.opentdc.tags.ServiceProvider#delete(java.lang.String)
	 */
	@Override
	public void delete(
		String id
	) throws NotFoundException, InternalServerErrorException {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.opentdc.tags.ServiceProvider#listTexts(java.lang.String, java.lang.String, java.lang.String, int, int)
	 */
	@Override
	public List<LocalizedTextModel> listTexts(
		String tid, 
		String queryType,
		String query, 
		int position, 
		int size
	) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.opentdc.tags.ServiceProvider#createText(java.lang.String, org.opentdc.service.LocalizedTextModel)
	 */
	@Override
	public LocalizedTextModel createText(
		String tid, 
		LocalizedTextModel tag
	) throws DuplicateException, ValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.opentdc.tags.ServiceProvider#readText(java.lang.String, java.lang.String)
	 */
	@Override
	public LocalizedTextModel readText(
		String tid, 
		String lid
	) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.opentdc.tags.ServiceProvider#updateText(java.lang.String, java.lang.String, org.opentdc.service.LocalizedTextModel)
	 */
	@Override
	public LocalizedTextModel updateText(
		String tid, 
		String id,
		LocalizedTextModel tag
	) throws NotFoundException, ValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.opentdc.tags.ServiceProvider#deleteText(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteText(
		String tid, 
		String id
	) throws NotFoundException, InternalServerErrorException {
		// TODO Auto-generated method stub
	}
	
}
