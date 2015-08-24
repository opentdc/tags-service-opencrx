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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
// import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.opencrx.kernel.code1.cci2.CodeValueContainerQuery;
import org.opencrx.kernel.code1.cci2.CodeValueEntryQuery;
import org.opencrx.kernel.code1.jmi1.CodeValueContainer;
import org.opencrx.kernel.code1.jmi1.CodeValueEntry;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.exception.ServiceException;
import org.opentdc.opencrx.AbstractOpencrxServiceProvider;
import org.opentdc.service.LocalizedTextModel;
import org.opentdc.service.exception.DuplicateException;
import org.opentdc.service.exception.InternalServerErrorException;
import org.opentdc.service.exception.NotFoundException;
import org.opentdc.service.exception.ValidationException;
import org.opentdc.tags.ServiceProvider;
import org.opentdc.tags.SingleLangTag;
import org.opentdc.tags.TagModel;
import org.opentdc.util.LanguageCode;

/**
 * Tags service for openCRX.
 *
 */
public class OpencrxServiceProvider extends AbstractOpencrxServiceProvider implements ServiceProvider {
	
	// private static final Logger logger = Logger.getLogger(OpencrxServiceProvider.class.getName());

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

	/**
	 * Map language to locale.
	 * 
	 * @param lang
	 * @return
	 */
	protected int getLocaleIndex(
		LanguageCode lang
	) {
		switch(lang) {
			case EN:
				return 0;
			case FR:
				return 7;
			case DE:
				return 1;
			case IT:
				return 8;
			case ES:
				return 2;
			case RM:
				return 28;
			default:
				return 0;
		}
	}

	/**
	 * Map code value entry to tag texts.
	 * 
	 * @param entry
	 * @param lang
	 * @return
	 */
	protected List<SingleLangTag> mapToTagTexts(
		CodeValueEntry entry,
		LanguageCode queryLang
	) {
		List<SingleLangTag> tagTexts = new ArrayList<SingleLangTag>();
		for(LanguageCode lang: LanguageCode.values()) {
			if(queryLang == null || queryLang == lang) {
				int localeIndex = this.getLocaleIndex(lang);
				List<String> texts = entry.getShortText();
				if(localeIndex < texts.size() && !texts.get(localeIndex).isEmpty()) {
					SingleLangTag tagText = new SingleLangTag();
					tagText.setLanguageCode(lang);
					tagText.setLocalizedTextId(lang.name());
					tagText.setTagId(entry.refGetPath().getLastSegment().toClassicRepresentation());
					tagText.setText(texts.get(localeIndex));
					tagTexts.add(tagText);
				}
			}
		}
		return tagTexts;
	}

	/**
	 * Map entry to localized texts.
	 * 
	 * @param entry
	 * @return
	 */
	protected List<LocalizedTextModel> mapToLocalizedTexts(
		CodeValueEntry entry,
		LanguageCode queryLang
	) {
		List<LocalizedTextModel> localizedTexts = new ArrayList<LocalizedTextModel>();
		for(LanguageCode lang: LanguageCode.values()) {
			if(queryLang == null || queryLang == lang) {			
				int localeIndex = this.getLocaleIndex(lang);
				List<String> texts = entry.getShortText();
				if(localeIndex < texts.size() && !texts.get(localeIndex).isEmpty()) {
					LocalizedTextModel localizedText = new LocalizedTextModel();
					localizedText.setCreatedAt(entry.getCreatedAt());
					localizedText.setCreatedBy(entry.getCreatedBy().get(0));
					localizedText.setModifiedAt(entry.getModifiedAt());
					localizedText.setModifiedBy(entry.getModifiedBy().get(0));
					localizedText.setId(lang.name());
					localizedText.setLanguageCode(lang);
					localizedText.setText(texts.get(localeIndex));
					localizedTexts.add(localizedText);
				}
			}
		}
		return localizedTexts;
	}

	/**
	 * Map code value container to tag.
	 * 
	 * @param codeValueEntry
	 * @return
	 */
	protected TagModel mapToTag(
		CodeValueEntry codeValueEntry
	) {
		TagModel tag = new TagModel();
		tag.setCreatedAt(codeValueEntry.getCreatedAt());
		tag.setCreatedBy(codeValueEntry.getCreatedBy().get(0));
		tag.setModifiedAt(codeValueEntry.getModifiedAt());
		tag.setModifiedBy(codeValueEntry.getModifiedBy().get(0));
		tag.setId(codeValueEntry.refGetPath().getLastSegment().toClassicRepresentation());
		return tag;
	}

	/**
	 * Get tags container.
	 * 
	 * @param codeSegment
	 * @return
	 */
	protected CodeValueContainer findTagsContainer(
		org.opencrx.kernel.code1.jmi1.Segment codeSegment
	) {
		PersistenceManager pm = JDOHelper.getPersistenceManager(codeSegment);
		CodeValueContainerQuery codeValueContainerQuery = (CodeValueContainerQuery)pm.newQuery(CodeValueContainer.class);
		codeValueContainerQuery.thereExistsName().equalTo("Tags");
		List<CodeValueContainer> codeValueContainers = codeSegment.getValueContainer(codeValueContainerQuery);
		if(codeValueContainers.isEmpty()) {
			try {
				pm.currentTransaction().begin();
				CodeValueContainer tagsContainer = pm.newInstance(CodeValueContainer.class);
				tagsContainer.setName("Tags");
				codeSegment.addValueContainer(
					Utils.getUidAsString(),
					tagsContainer
				);
				pm.currentTransaction().commit();
				return tagsContainer;
			} catch(Exception e) {
				try {
					pm.currentTransaction().rollback();
				} catch(Exception ignore) {}
				return null;
			}
		} else {
			return codeValueContainers.iterator().next();
		}		
	}

	/* (non-Javadoc)
	 * @see org.opentdc.tags.ServiceProvider#list(java.lang.String, java.lang.String, long, long)
	 */
	@Override
	public List<SingleLangTag> list(
		String query,
		String queryType,
		int position,
		int size
	) {
		PersistenceManager pm = this.getPersistenceManager();
		org.opencrx.kernel.code1.jmi1.Segment codeSegment = this.getCodeSegment();
		LanguageCode lang = null;
		if(query != null && !query.isEmpty()) {
			int pos = query.indexOf("lang=");
			if(pos >= 0) {
				try {
					lang = LanguageCode.valueOf(query.substring(pos + 5, pos + 7));
				} catch(Exception e) {
					throw new ValidationException("Invalid query " + query);					
				}
			} else {
				throw new ValidationException("Invalid query " + query);				
			}
		}
		List<SingleLangTag> tagTexts = new ArrayList<SingleLangTag>();
		CodeValueContainer tagsContainer = this.findTagsContainer(codeSegment);
		CodeValueEntryQuery codeValueEntryQuery = (CodeValueEntryQuery)pm.newQuery(CodeValueEntry.class);
		codeValueEntryQuery.orderByCreatedAt().ascending();
		codeValueEntryQuery.validTo().isNull();
		List<CodeValueEntry> entries = tagsContainer.getEntry(codeValueEntryQuery);
		for(Iterator<CodeValueEntry> i = entries.listIterator(position); i.hasNext(); ) {
			CodeValueEntry codeValueEntry = i.next();
			tagTexts.addAll(this.mapToTagTexts(codeValueEntry, lang));
			if(tagTexts.size() >= size) {
				break;
			}
		}
		return tagTexts;
	}

	/* (non-Javadoc)
	 * @see org.opentdc.tags.ServiceProvider#create(org.opentdc.tags.TagsModel)
	 */
	@Override
	public TagModel create(
		HttpServletRequest request,
		TagModel tag
	) throws DuplicateException, ValidationException {
		PersistenceManager pm = this.getPersistenceManager();
		org.opencrx.kernel.code1.jmi1.Segment codeSegment = this.getCodeSegment();
		CodeValueContainer tagsContainer = this.findTagsContainer(codeSegment);
		if(tag.getId() != null) {
			CodeValueEntry codeValueEntry = null;
			try {
				codeValueEntry = (CodeValueEntry)tagsContainer.getEntry(tag.getId());
			} catch(Exception ignore) {}
			if(codeValueEntry != null) {
				throw new DuplicateException("Tag with ID " + tag.getId() + " exists already.");			
			} else {
				throw new ValidationException("Tag <" + tag.getId() + "> contains an ID generated on the client. This is not allowed.");
			}
		}
		CodeValueEntry codeValueEntry = null;
		codeValueEntry = pm.newInstance(CodeValueEntry.class);
		try {
			pm.currentTransaction().begin();
			tagsContainer.addEntry(
				Utils.getUidAsString(),
				codeValueEntry
			);
			pm.currentTransaction().commit();
			return this.read(
				codeValueEntry.refGetPath().getLastSegment().toClassicRepresentation()
			);
		} catch(Exception e) {
			new ServiceException(e).log();
			try {
				pm.currentTransaction().rollback();
			} catch(Exception ignore) {}
			throw new InternalServerErrorException("Unable to create tag");
		}
	}

	/* (non-Javadoc)
	 * @see org.opentdc.tags.ServiceProvider#read(java.lang.String)
	 */
	@Override
	public TagModel read(
		String id
	) throws NotFoundException {
		org.opencrx.kernel.code1.jmi1.Segment codeSegment = this.getCodeSegment();
		CodeValueContainer tagsContainer = this.findTagsContainer(codeSegment);
		CodeValueEntry codeValueEntry = (CodeValueEntry)tagsContainer.getEntry(id);
		if(codeValueEntry == null || (codeValueEntry.getValidTo() != null && codeValueEntry.getValidTo().getTime() < System.currentTimeMillis())) {
			throw new org.opentdc.service.exception.NotFoundException(id);				
		}
		return this.mapToTag(codeValueEntry);
	}

	/* (non-Javadoc)
	 * @see org.opentdc.tags.ServiceProvider#update(java.lang.String, org.opentdc.tags.TagsModel)
	 */
	@Override
	public TagModel update(
		HttpServletRequest request,
		String id, 
		TagModel tag
	) throws NotFoundException, ValidationException {
		PersistenceManager pm = this.getPersistenceManager();
		org.opencrx.kernel.code1.jmi1.Segment codeSegment = this.getCodeSegment();
		CodeValueContainer tagsContainer = this.findTagsContainer(codeSegment);
		CodeValueEntry codeValueEntry = (CodeValueEntry)tagsContainer.getEntry(id);
		if(codeValueEntry == null) {
			throw new org.opentdc.service.exception.NotFoundException(id);				
		}
		try {
			pm.currentTransaction().begin();
			codeValueEntry.setEntryValue(new Date().toString());
			pm.currentTransaction().commit();
		} catch(Exception e) {
			new ServiceException(e).log();
			try {
				pm.currentTransaction().rollback();
			} catch(Exception ignore) {}
			throw new InternalServerErrorException("Unable to update tag");
		}
		return this.read(id);		
	}

	/* (non-Javadoc)
	 * @see org.opentdc.tags.ServiceProvider#delete(java.lang.String)
	 */
	@Override
	public void delete(
		String id
	) throws NotFoundException, InternalServerErrorException {
		PersistenceManager pm = this.getPersistenceManager();
		org.opencrx.kernel.code1.jmi1.Segment codeSegment = this.getCodeSegment();
		CodeValueContainer tagsContainer = this.findTagsContainer(codeSegment);
		CodeValueEntry codeValueEntry = (CodeValueEntry)tagsContainer.getEntry(id);
		if(codeValueEntry == null || (codeValueEntry.getValidTo() != null && codeValueEntry.getValidTo().getTime() < System.currentTimeMillis())) {
			throw new org.opentdc.service.exception.NotFoundException(id);				
		}
		try {
			pm.currentTransaction().begin();
			codeValueEntry.setValidTo(new Date());
			pm.currentTransaction().commit();
		} catch(Exception e) {
			new ServiceException(e).log();
			try {
				pm.currentTransaction().rollback();
			} catch(Exception ignore) {}
			throw new InternalServerErrorException("Unable to delete tag");
		}
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
		org.opencrx.kernel.code1.jmi1.Segment codeSegment = this.getCodeSegment();
		CodeValueContainer tagsContainer = this.findTagsContainer(codeSegment);
		CodeValueEntry codeValueEntry = (CodeValueEntry)tagsContainer.getEntry(tid);
		if(codeValueEntry == null || (codeValueEntry.getValidTo() != null && codeValueEntry.getValidTo().getTime() < System.currentTimeMillis())) {
			throw new org.opentdc.service.exception.NotFoundException(tid);				
		}
		return this.mapToLocalizedTexts(codeValueEntry, null);
	}

	/* (non-Javadoc)
	 * @see org.opentdc.tags.ServiceProvider#createText(java.lang.String, org.opentdc.service.LocalizedTextModel)
	 */
	@Override
	public LocalizedTextModel createText(
		HttpServletRequest request,
		String tid, 
		LocalizedTextModel tag
	) throws DuplicateException, ValidationException {
		PersistenceManager pm = this.getPersistenceManager();
		org.opencrx.kernel.code1.jmi1.Segment codeSegment = this.getCodeSegment();
		CodeValueContainer tagsContainer = this.findTagsContainer(codeSegment);
		CodeValueEntry codeValueEntry = (CodeValueEntry)tagsContainer.getEntry(tid);
		if(codeValueEntry == null || (codeValueEntry.getValidTo() != null && codeValueEntry.getValidTo().getTime() < System.currentTimeMillis())) {
			throw new org.opentdc.service.exception.NotFoundException(tid);				
		}
		if (tag.getText() == null || tag.getText().isEmpty()) {
			throw new ValidationException("LocalizedText <" + tid + "/lang/" + tag.getId() + "> must contain a valid text.");
		}
		// enforce that the title is a single word
		StringTokenizer _tokenizer = new StringTokenizer(tag.getText());
		if (_tokenizer.countTokens() != 1) {
			throw new ValidationException("LocalizedText <" + tid + "/lang/" + tag.getId() + "> must consist of exactly one word <" + tag.getText() + "> (is " + _tokenizer.countTokens() + ").");
		}
		if (tag.getLanguageCode() == null) {
			throw new ValidationException("LocalizedText <" + tid + "/lang/" + tag.getId() + 
					"> must contain a LanguageCode.");
		}		
		if(tag.getId() != null) {
			LocalizedTextModel localizedText = null;
			try {
				localizedText = this.readText(tid, tag.getId());
			} catch(Exception ignore) {}
			if(localizedText != null) {
				throw new DuplicateException("Localized text with ID " + tag.getId() + " exists already.");			
			} else {
				throw new ValidationException("Localized text <" + tag.getId() + "> contains an ID generated on the client. This is not allowed.");
			}
		}
		int localeIndex = this.getLocaleIndex(tag.getLanguageCode());
		if(localeIndex < codeValueEntry.getShortText().size() && !codeValueEntry.getShortText().get(localeIndex).isEmpty()) {
			throw new DuplicateException("LocalizedText with LanguageCode <" + tag.getLanguageCode() + "> exists already in tag <" + tid + ">.");			
		}
		try {
			pm.currentTransaction().begin();
			while(localeIndex >= codeValueEntry.getShortText().size()) {
				codeValueEntry.getShortText().add("");	
			}
			codeValueEntry.getShortText().set(
				this.getLocaleIndex(tag.getLanguageCode()),
				tag.getText()
			);
			pm.currentTransaction().commit();
			return this.readText(tid, tag.getLanguageCode().name());
		} catch(Exception e) {
			new ServiceException(e).log();
			try {
				pm.currentTransaction().rollback();
			} catch(Exception ignore) {}
			throw new InternalServerErrorException("Unable to create text");
		}
	}

	/* (non-Javadoc)
	 * @see org.opentdc.tags.ServiceProvider#readText(java.lang.String, java.lang.String)
	 */
	@Override
	public LocalizedTextModel readText(
		String tid, 
		String lid
	) throws NotFoundException {
		org.opencrx.kernel.code1.jmi1.Segment codeSegment = this.getCodeSegment();
		CodeValueContainer tagsContainer = this.findTagsContainer(codeSegment);
		CodeValueEntry codeValueEntry = (CodeValueEntry)tagsContainer.getEntry(tid);
		if(codeValueEntry == null || (codeValueEntry.getValidTo() != null && codeValueEntry.getValidTo().getTime() < System.currentTimeMillis())) {
			throw new org.opentdc.service.exception.NotFoundException(tid);				
		}
		List<LocalizedTextModel> localizedTexts = this.mapToLocalizedTexts(codeValueEntry, LanguageCode.valueOf(lid));
		if(localizedTexts.isEmpty()) {
			throw new org.opentdc.service.exception.NotFoundException(tid);			
		} else {
			return localizedTexts.get(0);
		}
	}

	/* (non-Javadoc)
	 * @see org.opentdc.tags.ServiceProvider#updateText(java.lang.String, java.lang.String, org.opentdc.service.LocalizedTextModel)
	 */
	@Override
	public LocalizedTextModel updateText(
		HttpServletRequest request,
		String tid, 
		String id,
		LocalizedTextModel tag
	) throws NotFoundException, ValidationException {
		PersistenceManager pm = this.getPersistenceManager();
		org.opencrx.kernel.code1.jmi1.Segment codeSegment = this.getCodeSegment();
		CodeValueContainer tagsContainer = this.findTagsContainer(codeSegment);
		CodeValueEntry codeValueEntry = (CodeValueEntry)tagsContainer.getEntry(tid);
		if(codeValueEntry == null || (codeValueEntry.getValidTo() != null && codeValueEntry.getValidTo().getTime() < System.currentTimeMillis())) {
			throw new org.opentdc.service.exception.NotFoundException(tid);				
		}
		List<LocalizedTextModel> localizedTexts = this.mapToLocalizedTexts(codeValueEntry, LanguageCode.valueOf(id));
		if(localizedTexts.isEmpty()) {
			throw new org.opentdc.service.exception.NotFoundException(tid);
		}
		if(tag.getLanguageCode() != LanguageCode.valueOf(id)) {
			throw new ValidationException("LocalizedText <" + tid + "/lang/" + id + ">: it is not allowed to change the LanguageCode.");			
		}
		try {
			pm.currentTransaction().begin();
			int localeIndex = this.getLocaleIndex(LanguageCode.valueOf(id));
			while(localeIndex >= codeValueEntry.getShortText().size()) {
				codeValueEntry.getShortText().add("");	
			}
			codeValueEntry.getShortText().set(
				localeIndex,
				tag.getText()
			);
			pm.currentTransaction().commit();
			return this.readText(tid, id);
		} catch(Exception e) {
			new ServiceException(e).log();
			try {
				pm.currentTransaction().rollback();
			} catch(Exception ignore) {}
			throw new InternalServerErrorException("Unable to update text");			
		}
	}

	/* (non-Javadoc)
	 * @see org.opentdc.tags.ServiceProvider#deleteText(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteText(
		String tid, 
		String id
	) throws NotFoundException, InternalServerErrorException {
		PersistenceManager pm = this.getPersistenceManager();
		org.opencrx.kernel.code1.jmi1.Segment codeSegment = this.getCodeSegment();
		CodeValueContainer tagsContainer = this.findTagsContainer(codeSegment);
		CodeValueEntry codeValueEntry = (CodeValueEntry)tagsContainer.getEntry(tid);
		if(codeValueEntry == null || (codeValueEntry.getValidTo() != null && codeValueEntry.getValidTo().getTime() < System.currentTimeMillis())) {
			throw new org.opentdc.service.exception.NotFoundException(tid);				
		}
		List<LocalizedTextModel> localizedTexts = this.mapToLocalizedTexts(codeValueEntry, LanguageCode.valueOf(id));
		if(localizedTexts.isEmpty()) {
			throw new org.opentdc.service.exception.NotFoundException(tid);
		}
		try {
			pm.currentTransaction().begin();
			int localeIndex = this.getLocaleIndex(LanguageCode.valueOf(id));
			while(localeIndex >= codeValueEntry.getShortText().size()) {
				codeValueEntry.getShortText().add("");	
			}
			codeValueEntry.getShortText().set(
				localeIndex,
				""
			);
			pm.currentTransaction().commit();
		} catch(Exception e) {
			new ServiceException(e).log();
			try {
				pm.currentTransaction().rollback();
			} catch(Exception ignore) {}
			throw new InternalServerErrorException("Unable to delete text");			
		}
	}
	
}
