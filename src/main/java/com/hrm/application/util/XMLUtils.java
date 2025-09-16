package com.hrm.application.util;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class XMLUtils {
    private static final Logger logger = LoggerFactory.getLogger(XMLUtils.class);
    private static XmlMapper getNoRootXmlMapper() {
        return new XmlMapper();
    }
    private static XmlMapper getWithRootXmlMapper(){
        XmlMapper xmlMapper = new XmlMapper();

        xmlMapper.registerModule(new SimpleModule().addDeserializer(JsonNode.class, new JsonNodeDeserializer() {
            @Override
            public JsonNode deserialize(JsonParser p, DeserializationContext context) throws IOException {
                String rootName = ((FromXmlParser) p).getStaxReader().getLocalName();
                return context.getNodeFactory().objectNode().set(rootName, super.deserialize(p, context));
            }
        }));
        return xmlMapper;
    }

    public static JsonNode xml2json(String xml) {
        try {
            return getNoRootXmlMapper().readTree(xml.getBytes());
        } catch (IOException e) {
            logger.debug("error xml:\n{}", xml);
            return getNoRootXmlMapper().nullNode();
        }
    }
    public static JsonNode xml2jsonWithRoot(String xml) throws IOException {
        try {
            return getWithRootXmlMapper().readTree(xml.getBytes());
        } catch (IOException e) {
            logger.debug("error xml:\n{}", xml);
            return getNoRootXmlMapper().nullNode();
        }
    }
}
