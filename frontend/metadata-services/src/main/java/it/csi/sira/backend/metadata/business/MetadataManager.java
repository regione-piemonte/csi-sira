package it.csi.sira.backend.metadata.business;


import it.csi.sira.backend.metadata.dto.JsonAppCategory;
import it.csi.sira.backend.metadata.dto.JsonInfoBox;
import it.csi.sira.backend.metadata.dto.JsonMetaObject;
import it.csi.sira.backend.metadata.dto.JsonPlatformNumbers;
import it.csi.sira.backend.metadata.exception.MetadataManagerException;
import it.csi.sira.backend.metadata.integration.custom.dto.InfoBoxDTO;
import it.csi.sira.backend.metadata.integration.dto.SipraMtdDFontedati;
import it.csi.sira.backend.metadata.integration.dto.SipraMtdDTipoFunzione;
import it.csi.sira.backend.metadata.integration.dto.SipraMtdRCategLingua;
import it.csi.sira.backend.metadata.integration.dto.SipraMtdRCategappCategori;
import it.csi.sira.backend.metadata.integration.dto.SipraMtdTCategoriaAppl;
import it.csi.sira.backend.metadata.integration.dto.SipraMtdTFunzione;
import it.csi.sira.backend.metadata.integration.dto.SipraMtdTMtdCsw;
import it.csi.sira.backend.metadata.integration.servizi.csw.CswAdapter;
import it.csi.sira.backend.metadata.integration.servizi.csw.CswService;
import it.csi.sira.backend.metadata.integration.servizi.csw.dto.CswRecord;
import it.csi.sira.backend.metadata.integration.servizi.csw.exception.CswAdapterException;
import it.csi.sira.backend.metadata.integration.servizi.csw.exception.CswServiceException;
import it.csi.sira.backend.metadata.utils.Constants;
import it.csi.sira.backend.metadata.utils.IntegratioManager;
import it.csi.sira.backend.metadata.utils.LogFormatter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MetadataManager {
	private static String className = MetadataManager.class.getSimpleName();

	private TransactionTemplate transactionTemplate = null;
	private IntegratioManager integratioManager = null;

	private CswService cswService = null;
	private CswAdapter cswAdapter = null;

	private Properties logger = null;
	
	public String getPlatformNumbers() throws MetadataManagerException {

		final String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();

		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "BEGIN"));

		Map<String, Object> params = null;
		String json = null;
		JsonPlatformNumbers numbers = new JsonPlatformNumbers();

		try {

			String query = integratioManager.getQueries().getProperty(
					"GET_SIRADEC_OBJECT_NUMBER");

			params = new HashMap<String, Object>();

			List<Long> siradecObjectNumber = integratioManager.getDaoManager()
					.getSipraMtdTMtdPlusDAO()
					.findByGenericCriteria(query, new LongRowMapper(), params);

			if (siradecObjectNumber != null && siradecObjectNumber.size() == 1) {
				numbers.setSiradecObject((siradecObjectNumber.get(0)));
			}

			query = integratioManager.getQueries().getProperty(
					"GET_FUNCTION_OBJECT_NUMBER");

			params = new HashMap<String, Object>();
			params.put("fk_tipo_funzione", Constants.FUNZIONE_MAPPA);
			List<Long> functionObjectMapNumber = integratioManager
					.getDaoManager().getSipraMtdTFunzioneDAO()
					.findByGenericCriteria(query, new LongRowMapper(), params);

			if (functionObjectMapNumber != null
					&& functionObjectMapNumber.size() == 1) {
				numbers.setFunctionObjectMap((functionObjectMapNumber.get(0)));
			}

			params = new HashMap<String, Object>();
			params.put("fk_tipo_funzione", Constants.FUNZIONE_CERCA);
			List<Long> functionObjectSearchNumber = integratioManager
					.getDaoManager().getSipraMtdTFunzioneDAO()
					.findByGenericCriteria(query, new LongRowMapper(), params);

			if (functionObjectSearchNumber != null
					&& functionObjectSearchNumber.size() == 1) {
				numbers.setFunctionObjectSearch((functionObjectSearchNumber
						.get(0)));
			}

			params = new HashMap<String, Object>();
			params.put("fk_tipo_funzione", Constants.FUNZIONE_VISTA);
			List<Long> functionObjectViewNumber = integratioManager
					.getDaoManager().getSipraMtdTFunzioneDAO()
					.findByGenericCriteria(query, new LongRowMapper(), params);

			if (functionObjectViewNumber != null
					&& functionObjectViewNumber.size() == 1) {
				numbers.setFunctionObjectView((functionObjectViewNumber.get(0)));
			}

			if (numbers != null) {
				json = getGsonInstance().toJson(numbers);
			}

		} catch (Exception e) {
			Logger.getLogger(logger.getProperty("LOGGER_NAME")).error(
					LogFormatter.format(className, methodName, e.getMessage()));
			throw new MetadataManagerException(e);
		}

		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "END"));

		return json;
	}

	public String getInfoBox(int idMetadato) throws MetadataManagerException {

		final String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();

		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "BEGIN"));

		String json = null;

		try {

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("id_metadato", idMetadato);

			String query = integratioManager.getQueries().getProperty(
					"GET_INFO_BOX");

			RowMapper<InfoBoxDTO> rowMapper = integratioManager.getDaoManager()
					.getInfoBoxDAO().getRowMapper();

			List<InfoBoxDTO> elementiInfoBox = integratioManager
					.getDaoManager().getInfoBoxDAO()
					.findByGenericCriteria(query, rowMapper, params);

			if (elementiInfoBox != null && elementiInfoBox.size() > 0) {
				if (elementiInfoBox.size() == 1) {

					JsonInfoBox jsonInfoBox = new JsonInfoBox();
					jsonInfoBox.setTitle(elementiInfoBox.get(0).getTitolo());
					jsonInfoBox.setText(elementiInfoBox.get(0)
							.getTestoAbstract());
					jsonInfoBox.setDataProvider(elementiInfoBox.get(0)
							.getDesFontedati());
					jsonInfoBox.setDataLastUpdate(elementiInfoBox.get(0)
							.getDataUltAgg());
					jsonInfoBox.setNumDatasetObjectCalc(elementiInfoBox.get(0)
							.getNrOggettiDatasetCalc());
					jsonInfoBox.setUrlMetadatoCalc(elementiInfoBox.get(0)
							.getUrlMetadatoCalc());

					params = new HashMap<String, Object>();
					params.put("fk_metadato", idMetadato);
					params.put("fk_tipo_funzione", Constants.FUNZIONE_MAPPA);
					List<SipraMtdTFunzione> functionWMS = integratioManager
							.getDaoManager().getSipraMtdTFunzioneDAO()
							.findByCriteria(params);

					if (functionWMS != null && functionWMS.size() > 0) {

						jsonInfoBox.setUrlWMS(new String[functionWMS.size()]);

						for (int i = 0; i < functionWMS.size(); i++) {
							jsonInfoBox.getUrlWMS()[i] = functionWMS.get(i)
									.getRequestUrl();
						}
					}

					params = new HashMap<String, Object>();
					params.put("fk_metadato", idMetadato);
					params.put("fk_tipo_funzione",
							Constants.FUNZIONE_DOWNLOAD);
					List<SipraMtdTFunzione> functionWFS = integratioManager
							.getDaoManager().getSipraMtdTFunzioneDAO()
							.findByCriteria(params);

					if (functionWFS != null && functionWFS.size() > 0) {

						jsonInfoBox.setUrlWFS(new String[functionWFS.size()]);

						for (int i = 0; i < functionWFS.size(); i++) {
							jsonInfoBox.getUrlWFS()[i] = functionWFS.get(i)
									.getRequestUrl();
						}
					}

					json = getGsonInstance().toJson(jsonInfoBox);

				} else {
					throw new MetadataManagerException(
							"Too much info box element!!");
				}
			}

		} catch (Exception e) {
			Logger.getLogger(logger.getProperty("LOGGER_NAME")).error(
					LogFormatter.format(className, methodName, e.getMessage()));
			throw new MetadataManagerException(e);
		}

		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "END"));

		return json;
	}

	public String getMosaico() throws MetadataManagerException {

		final String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();

		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "BEGIN"));

		String json = null;

		JsonAppCategory[] categories = null;
		Map<String, Object> params = null;

		params = new HashMap<String, Object>();
		params.put("livello", 1);

		List<SipraMtdTCategoriaAppl> allFirstCategories = null;

		try {
			allFirstCategories = integratioManager.getDaoManager()
					.getSipraMtdTCategoriaApplDAO().findByCriteria(params);

			if (allFirstCategories != null && allFirstCategories.size() > 0) {

				categories = new JsonAppCategory[allFirstCategories.size()];

				for (int i = 0; i < allFirstCategories.size(); i++) {

					JsonAppCategory appCategory = new JsonAppCategory();

					appCategory.setId(allFirstCategories.get(i)
							.getIdCategoriaAppl());
					appCategory.setName(allFirstCategories.get(i)
							.getDesCategoria());
					appCategory
							.setIcon(allFirstCategories.get(i).getUrlIcona());
					appCategory.setObjectNumber(allFirstCategories.get(i)
							.getObjectNumber() != null ? allFirstCategories
							.get(i).getObjectNumber() : 0);
					appCategory.setTematicViewNumber(allFirstCategories.get(i)
							.getViewNumber() != null ? allFirstCategories
							.get(i).getViewNumber() : 0);

					categories[i] = appCategory;
				}
			}

			if (categories != null) {
				json = getGsonInstance().toJson(categories);
			}

		} catch (Exception e) {
			Logger.getLogger(logger.getProperty("LOGGER_NAME")).error(
					LogFormatter.format(className, methodName, e.getMessage()));
			throw new MetadataManagerException(e);
		}

		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "END"));

		return json;
	}

	private JsonMetaObject getAppCategories(Integer idCategoria, String text,
			String type) throws MetadataManagerException {

		final String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();

		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "BEGIN"));

		JsonMetaObject appCategory = null;
		Map<String, Object> params = null;

		try {
			SipraMtdTCategoriaAppl appCat = integratioManager.getDaoManager()
					.getSipraMtdTCategoriaApplDAO().findByPK(idCategoria);

			if (appCat != null) {

				appCategory = new JsonMetaObject();
				appCategory.setTitle(appCat.getDesCategoria());

			} else {
				throw new MetadataManagerException(
						"Category not found in sipra_mtd_t_sottocategoria!!");
			}

			params = new HashMap<String, Object>();
			params.put("fk_padre", idCategoria);
			List<SipraMtdTCategoriaAppl> appCategories = integratioManager
					.getDaoManager().getSipraMtdTCategoriaApplDAO()
					.findByCriteria(params);

			if (appCategories != null && appCategories.size() > 0) {

				JsonMetaObject[] children = new JsonMetaObject[appCategories
						.size()];

				for (int i = 0; i < appCategories.size(); i++) {
					children[i] = getAppCategories(appCategories.get(i)
							.getIdCategoriaAppl(), text, type);
				}

				appCategory.setCategories(children);

				if (appCategory.getCategories() != null) {
					for (int c = 0; c < appCategory.getCategories().length; c++) {
						appCategory.setObjectCounter(appCategory
								.getCategories()[c].getObjectCounter()
								+ appCategory.getObjectCounter());
						appCategory.setTematicViewCounter(appCategory
								.getCategories()[c].getTematicViewCounter()
								+ appCategory.getTematicViewCounter());
					}
				}

			} else {

				appCategory.setCategories(getMetaCategories(idCategoria, text,
						type));

				if (appCategory.getCategories() != null) {
					for (int i = 0; i < appCategory.getCategories().length; i++) {
						appCategory.setObjectCounter(appCategory
								.getCategories()[i].getObjectCounter()
								+ appCategory.getObjectCounter());
						appCategory.setTematicViewCounter(appCategory
								.getCategories()[i].getTematicViewCounter()
								+ appCategory.getTematicViewCounter());
					}
				}
			}
		} catch (Exception e) {
			Logger.getLogger(logger.getProperty("LOGGER_NAME")).error(
					LogFormatter.format(className, methodName, e.getMessage()));
			throw new MetadataManagerException(e);
		}

		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "END"));

		return appCategory;
	}

	private JsonMetaObject[] getMetaCategories(int idCategoria, String text,
			String type) throws MetadataManagerException {

		final String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();

		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "BEGIN"));

		JsonMetaObject[] children = null;

		Map<String, Object> params = null;
		params = new HashMap<String, Object>();
		params.put("id_categoria_appl", idCategoria);

		try {

			List<SipraMtdRCategappCategori> metaCategories = integratioManager
					.getDaoManager().getSipraMtdRCategappCategoriDAO()
					.findByCriteria(params);

			if (metaCategories != null && metaCategories.size() > 0) {

				children = new JsonMetaObject[metaCategories.size()];

				for (int i = 0; i < metaCategories.size(); i++) {

					SipraMtdRCategappCategori sipraMtdRCategappCategori = metaCategories
							.get(i);

					SipraMtdRCategLingua metaCat = integratioManager
							.getDaoManager()
							.getSipraMtdRCategLinguaDAO()
							.findByPK(
									sipraMtdRCategappCategori.getIdCategoria(),
									1);

					JsonMetaObject metaCategory = new JsonMetaObject();
					metaCategory.setTitle(metaCat.getDesCategoria());

					if ("S".equals(metaCat.getFlAlias())) {
						metaCategory.setTitle(metaCat.getDesAlias());
					}

					metaCategory.setObjectCounter(0);
					metaCategory.setTematicViewCounter(0);

					if (type.equals(Constants.METADATA_OBJECTS)) {
						JsonMetaObject[] metaObjects = getMetadataObjects(
								sipraMtdRCategappCategori.getIdCategoria(),
								text);

						if (metaObjects != null) {
							metaCategory.setObjectCounter(metaObjects.length);
							metaCategory.setMetadata(metaObjects);
						}
					} else if (type.equals(Constants.METADATA_VIEWS)) {
						JsonMetaObject[] metaViews = getMetadataTematicViews(
								sipraMtdRCategappCategori.getIdCategoria(),
								text);

						if (metaViews != null) {
							metaCategory
									.setTematicViewCounter(metaViews.length);
							metaCategory.setMetadata(metaViews);
						}
					}

					children[i] = metaCategory;
				}
			}
		} catch (Exception e) {
			Logger.getLogger(logger.getProperty("LOGGER_NAME")).error(
					LogFormatter.format(className, methodName, e.getMessage()));
			throw new MetadataManagerException(e);
		}

		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "END"));

		return children;
	}

	private JsonMetaObject[] getMetadataObjects(int idCategory, String text)
			throws MetadataManagerException {

		final String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();

		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "BEGIN"));

		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "idCategory: "
						+ idCategory));
		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "text: " + text));

		String query = null;
		RowMapper<SipraMtdTMtdCsw> rowMapper = integratioManager
				.getDaoManager().getSipraMtdTMtdCswDAO().getRowMapper();

		JsonMetaObject[] children = null;
		Map<String, Object> params = null;
		params = new HashMap<String, Object>();

		List<SipraMtdTMtdCsw> metadata = null;

		if (text != null && !text.equals("")) {

			params.put("id_categoria", idCategory);
			params.put("text", "%" + text.trim() + "%");

			query = integratioManager.getQueries().getProperty(
					"GET_METADATA_OBJECTS_BY_ID_CATEDORIA_AND_TEXT");

		} else {
			params.put("id_categoria", idCategory);
			query = integratioManager.getQueries().getProperty(
					"GET_METADATA_OBJECTS_BY_ID_CATEGORIA");
		}

		try {
			metadata = integratioManager.getDaoManager()
					.getSipraMtdTMtdCswDAO()
					.findByGenericCriteria(query, rowMapper, params);

			if (metadata != null && metadata.size() > 0) {
				children = new JsonMetaObject[metadata.size()];

				for (int i = 0; i < metadata.size(); i++) {

					JsonMetaObject o = new JsonMetaObject();

					o.setId(metadata.get(i).getIdMetadato());
					o.setTitle(metadata.get(i).getTitolo());
					o.setText(metadata.get(i).getTestoAbstract());

					o.setFunctions(getFunctions(metadata.get(i).getIdMetadato()));

					o.setObjectCounter(null);
					o.setTematicViewCounter(null);

					children[i] = o;
				}
			}

		} catch (Exception e) {
			Logger.getLogger(logger.getProperty("LOGGER_NAME")).error(
					LogFormatter.format(className, methodName, e.getMessage()));
			throw new MetadataManagerException(e);
		}

		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "END"));

		return children;
	}

	private JsonMetaObject[] getMetadataTematicViews(int idCategory, String text)
			throws MetadataManagerException {

		final String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();

		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "BEGIN"));

		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "idCategory: "
						+ idCategory));
		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "text: " + text));

		String query = null;
		RowMapper<SipraMtdTMtdCsw> rowMapper = integratioManager
				.getDaoManager().getSipraMtdTMtdCswDAO().getRowMapper();

		JsonMetaObject[] children = null;
		Map<String, Object> params = null;
		params = new HashMap<String, Object>();

		List<SipraMtdTMtdCsw> metadata = null;

		if (text != null && !text.equals("")) {

			params.put("id_categoria", idCategory);
			params.put("text", "%" + text.trim() + "%");

			query = integratioManager.getQueries().getProperty(
					"GET_METADATA_VIEWS_BY_ID_CATEDORIA_AND_TEXT");

		} else {
			params.put("id_categoria", idCategory);
			query = integratioManager.getQueries().getProperty(
					"GET_METADATA_VIEWS_BY_ID_CATEGORIA");
		}

		try {
			metadata = integratioManager.getDaoManager()
					.getSipraMtdTMtdCswDAO()
					.findByGenericCriteria(query, rowMapper, params);

			if (metadata != null && metadata.size() > 0) {
				children = new JsonMetaObject[metadata.size()];

				for (int i = 0; i < metadata.size(); i++) {

					JsonMetaObject o = new JsonMetaObject();

					o.setId(metadata.get(i).getIdMetadato());
					o.setTitle(metadata.get(i).getTitolo());
					o.setObjectCounter(null);
					o.setText(metadata.get(i).getTestoAbstract());

					o.setFunctions(getFunctions(metadata.get(i).getIdMetadato()));

					o.setObjectCounter(null);
					o.setTematicViewCounter(null);

					children[i] = o;
				}
			}

		} catch (Exception e) {
			Logger.getLogger(logger.getProperty("LOGGER_NAME")).error(
					LogFormatter.format(className, methodName, e.getMessage()));
			throw new MetadataManagerException(e);

		}

		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "END"));

		return children;
	}

	private JsonMetaObject[] getFunctions(int idMetadato)
			throws MetadataManagerException {

		final String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();

		JsonMetaObject[] children = null;

		Map<String, Object> params = null;
		params = new HashMap<String, Object>();
		params.put("fk_metadato", idMetadato);

		try {
			List<SipraMtdTFunzione> functions = integratioManager
					.getDaoManager().getSipraMtdTFunzioneDAO()
					.findByCriteria(params);

			if (functions != null && functions.size() > 0) {
				children = new JsonMetaObject[functions.size()];

				for (int i = 0; i < functions.size(); i++) {

					JsonMetaObject o = new JsonMetaObject();

					o.setUrl(functions.get(i).getRequestUrl());
					o.setObjectCounter(null);
					o.setTematicViewCounter(null);

					SipraMtdDTipoFunzione type = integratioManager
							.getDaoManager().getSipraMtdDTipoFunzioneDAO()
							.findByPK(functions.get(i).getFkTipoFunzione());

					if (type != null) {
						o.setType(type.getDesTipoFunzione());
					}

					children[i] = o;
				}
			}

		} catch (Exception e) {
			Logger.getLogger(logger.getProperty("LOGGER_NAME")).error(
					LogFormatter.format(className, methodName, e.getMessage()));
			throw new MetadataManagerException(e);

		}

		return children;
	}

	public String getMetadata(Integer idCategory, String text, String type)
			throws MetadataManagerException {

		final String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();

		String json = null;

		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "BEGIN"));

		if (idCategory == null) {
			throw new MetadataManagerException("Category not valid!!");
		}

		try {

			SipraMtdTCategoriaAppl sipraMtdTCategoriaAppl = integratioManager
					.getDaoManager().getSipraMtdTCategoriaApplDAO()
					.findByPK(idCategory);

			if (sipraMtdTCategoriaAppl == null) {
				throw new MetadataManagerException("Category not exist!!");
			}

			JsonMetaObject category = getAppCategories(
					sipraMtdTCategoriaAppl.getIdCategoriaAppl(), text, type);

			if (category != null) {
				json = getGsonInstance().toJson(category.getCategories());
			}
		} catch (Exception e) {
			Logger.getLogger(logger.getProperty("LOGGER_NAME")).error(
					LogFormatter.format(className, methodName, e.getMessage()));
			throw new MetadataManagerException(e);
		}

		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "END"));

		return json;
	}

	private List<CswRecord> filterOnCategory(List<CswRecord> cswRecords) {
		final String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();

		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "BEGIN"));

		List<CswRecord> cswRecordsValid = new ArrayList<CswRecord>();

		Map<String, Integer> categorieMap = new HashMap<String, Integer>();

		List<SipraMtdRCategLingua> categorie = integratioManager
				.getDaoManager().getSipraMtdRCategLinguaDAO().findAll();

		for (SipraMtdRCategLingua categoria : categorie) {
			categorieMap.put((String) categoria.getDesCategoria(),
					(Integer) categoria.getIdCategoria());
		}

		// cerco i record validi ossia quelli che tra i subject hanno almeno
		// una categoria tra quelle passate in input

		for (int i = 0; i < cswRecords.size(); i++) {

			CswRecord recordCSW = cswRecords.get(i);

			boolean isValid = false;

			for (int s = 0; s < recordCSW.getSubjects().length; s++) {

				if (categorieMap.containsKey(recordCSW.getSubjects()[s]
						.getTesto())) {
					Integer idCategoria = categorieMap.get(recordCSW
							.getSubjects()[s].getTesto());
					recordCSW.getSubjects()[s].setIdCategoria(idCategoria);
					isValid = true;
				}
			}

			if (isValid) {
				cswRecordsValid.add(recordCSW);
			}
		}

		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "END"));

		return cswRecordsValid;
	}

	public String searchCswMetadata(String txt, String service,
			int startPosition, int maxRecords) throws CswServiceException,
			CswAdapterException {
		final String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();

		String json = null;

		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "BEGIN"));

		Map<String, Object> params = null;

		params = new HashMap<String, Object>();
		params.put("fl_attiva", "S");
		List<SipraMtdDFontedati> elencoFontiDati = integratioManager
				.getDaoManager().getSipraMtdDFontedatiDAO()
				.findByCriteria(params);

		// **************************

		List<CswRecord> rtn = new ArrayList<CswRecord>();

		if (elencoFontiDati != null && elencoFontiDati.size() > 0) {

			for (int f = 0; f < elencoFontiDati.size(); f++) {
				cswService.setUrlService(elencoFontiDati.get(f)
						.getUrlServizio());

				String xmlCSW = cswService.getRecords(txt, startPosition,
						maxRecords);

				List<CswRecord> cswValidRecords = null;

				List<CswRecord> cswRecords = cswAdapter.getCswRecords(xmlCSW,
						elencoFontiDati.get(f));

				if (cswRecords != null) {
					cswValidRecords = this.filterOnCategory(cswRecords);
				}

				if (cswValidRecords != null) {
					rtn.addAll(cswValidRecords);
				}
			}
		}

		if (rtn.size() > 0) {
			json = getGsonInstance().toJson(
					rtn.toArray(new CswRecord[rtn.size()]));
		}

		Logger.getLogger(logger.getProperty("LOGGER_NAME")).debug(
				LogFormatter.format(className, methodName, "END"));

		return json;
	}

	private Gson getGsonInstance() {
		// return new Gson();
		return new GsonBuilder().disableHtmlEscaping().create();
	}

	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public IntegratioManager getIntegratioManager() {
		return integratioManager;
	}

	public void setIntegratioManager(IntegratioManager integratioManager) {
		this.integratioManager = integratioManager;
	}

	public CswService getCswService() {
		return cswService;
	}

	public void setCswService(CswService cswService) {
		this.cswService = cswService;
	}

	public CswAdapter getCswAdapter() {
		return cswAdapter;
	}

	public void setCswAdapter(CswAdapter cswAdapter) {
		this.cswAdapter = cswAdapter;
	}

	public Properties getLogger() {
		return logger;
	}

	public void setLogger(Properties logger) {
		this.logger = logger;
	}
	
	class LongRowMapper implements RowMapper<Long> {

		public LongRowMapper() {

		}

		public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getLong("object_numbers");
		}
	}

}
