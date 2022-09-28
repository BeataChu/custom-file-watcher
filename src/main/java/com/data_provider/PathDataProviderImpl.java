package com.data_provider;

import com.data_provider.dao.MirrorPathData;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

public class PathDataProviderImpl implements PathDataProvider {

    @Override
    public MirrorPathData getPathDataForProject() {
        MirrorPathData mirrorPathData = null;
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("mirror_folders_config.json")) {
            ObjectMapper mapper = new ObjectMapper();
            mirrorPathData = mapper.readValue(in, MirrorPathData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mirrorPathData;
    }
}
