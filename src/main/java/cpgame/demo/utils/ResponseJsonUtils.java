package cpgame.demo.utils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: JsonUtils
 * @Description:
 * @author chenpeng
 * @date 2012-8-30 下午12:13:22
 * 
 */
public class ResponseJsonUtils {
	private static Logger logger = LoggerFactory.getLogger(ResponseJsonUtils.class);

	public static String object2json(Object obj) {
		StringBuilder json = new StringBuilder();
		if (obj == null) {
			json.append("null");
		} else if (obj instanceof Integer || obj instanceof Float
				|| obj instanceof Boolean || obj instanceof Short
				|| obj instanceof Double || obj instanceof Long
				|| obj instanceof BigDecimal || obj instanceof BigInteger
				|| obj instanceof Byte) {
			json.append(string2json(obj.toString()));
		} else if (obj instanceof String) {
			json.append("\"").append(string2json(obj.toString())).append("\"");
		} else if (obj instanceof Object[]) {
			json.append(array2json((Object[]) obj));
		} else if (obj instanceof List) {
			json.append(list2json((List<?>) obj));
		} else if (obj instanceof Map) {
			json.append(map2json((Map<?, ?>) obj));
		} else if (obj instanceof Set) {
			json.append(set2json((Set<?>) obj));
		} else if (obj.getClass().isArray()) {
			json.append(array2json(obj));
		} else if (obj instanceof Collection<?>) {
			json.append(collection2json((Collection<?>) obj));
		} else {
			json.append(bean2json(obj));
		}
		return json.toString();
	}

	public static String bean2json_bk(Object bean) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		PropertyDescriptor[] props = null;
		try {
			props = Introspector.getBeanInfo(bean.getClass(), Object.class)
					.getPropertyDescriptors();
		} catch (IntrospectionException e) {
			logger.error("", e);
		}
		if (props != null) {
			for (int i = 0; i < props.length; i++) {
				try {
					String value = object2json(props[i].getReadMethod().invoke(
							bean));
					json.append(value);
					json.append(",");
				} catch (Exception e) {
					logger.error("", e);
				}
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	public static Object getFieldValueByName(Field field, Object o)
			throws Exception {

		String fieldName = field.getName();
		Object value = PropertyUtils.getProperty(o, fieldName);
		return value;

	}

	public static String bean2json(Object bean) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		Field[] fields = bean.getClass().getDeclaredFields();
		if (fields != null) {
			for (int i = 0; i < fields.length; i++) {
				try {
					if (!"serialVersionUID".equals(fields[i].getName())) {
						Object o = getFieldValueByName(fields[i], bean);
						String value = object2json(o);
						json.append(value);
						json.append(",");
					}
				} catch (Exception e) {
					logger.error("", e);
				}
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	public static String bean2jsonWithOutSomething(Object bean) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		Field[] fields = bean.getClass().getDeclaredFields();
		if (fields != null) {
			for (int i = 0; i < fields.length; i++) {
				try {
					if (!"serialVersionUID".equals(fields[i].getName())
							&&!Modifier.isStatic((fields[i].getModifiers()))
							&&!Modifier.isFinal((fields[i].getModifiers()))
							&&!Modifier.isAbstract((fields[i].getModifiers()))
							&&!Modifier.isInterface((fields[i].getModifiers()))
							&&!Modifier.isNative((fields[i].getModifiers()))
							&&!Modifier.isAbstract((fields[i].getModifiers()))
							) {
						Object o = getFieldValueByName(fields[i], bean);
						String value = object2json(o);
						json.append(value);
						json.append(",");
					}
				} catch (Exception e) {
					logger.error("", e);
				}
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}
	public static String list2json(List<?> list) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				json.append(object2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	public static String collection2json(Collection<?> list) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				json.append(object2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	public static String array2json(Object[] array) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (array != null && array.length > 0) {
			for (Object obj : array) {
				json.append(object2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	public static String array2json(Object array) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (array != null && Array.getLength(array) > 0) {
			for (int i = 0; i < Array.getLength(array); i++) {
				Object obj = Array.get(array, i);
				json.append(object2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	public static String map2json(Map<?, ?> map) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (map != null && map.size() > 0) {
			for (Object key : map.keySet()) {
				json.append(object2json(map.get(key)));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	public static String set2json(Set<?> set) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (set != null && set.size() > 0) {
			for (Object obj : set) {
				json.append(object2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	public static String string2json(String s) {
		if (s == null)
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			// case '/':
			// sb.append("\\/");
			// break;
			default:
				if (ch >= '\u0000' && ch <= '\u001F') {
					String ss = Integer.toHexString(ch);
					sb.append("\\u");
					for (int k = 0; k < 4 - ss.length(); k++) {
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				} else {
					sb.append(ch);
				}
			}
		}
		return replaceQualityColor(sb.toString());
	}

	public static String replaceQualityColor(String str) {
		str = str.replaceAll("#0#", "#ffffff");
		str = str.replaceAll("#1#", "#009900");
		str = str.replaceAll("#2#", "#0099ff");
		str = str.replaceAll("#3#", "#9900ff");
		str = str.replaceAll("#4#", "#ff9900");
		str = str.replaceAll("#6#", "#9C661F");
		return str;
	}

}
