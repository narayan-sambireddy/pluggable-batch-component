package narayan.components.eodbatch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * @author narayana
 *
 */
@Component
@ConfigurationProperties(prefix = "eodbatch")
class EODBatchProperties {

    private String sql;
    private String filePath;
    private String fileNamePrefix;
    private String delimiter;
    private String headerMarker;
    private String footerMarker;
    private String fieldsList[];
    private Integer fetchSize;
    private Integer commitInterval;


    public Integer getCommitInterval() {
        return commitInterval;
    }

    public void setCommitInterval(Integer commitInterval) {
        this.commitInterval = commitInterval;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Integer getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(Integer fetchSize) {
        this.fetchSize = fetchSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileNamePrefix() {
        return fileNamePrefix;
    }

    public void setFileNamePrefix(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String[] getFieldsList() {
        return fieldsList;
    }

    public void setFieldsList(String[] fieldsList) {
        this.fieldsList = fieldsList;
    }

    public String getHeaderMarker() {
        return headerMarker;
    }

    public void setHeaderMarker(String headerMarker) {
        this.headerMarker = headerMarker;
    }

    public String getFooterMarker() {
        return footerMarker;
    }

    public void setFooterMarker(String footerMarker) {
        this.footerMarker = footerMarker;
    }
}
