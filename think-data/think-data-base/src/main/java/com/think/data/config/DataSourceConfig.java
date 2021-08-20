package com.think.data.config;



/**
 * 默认的 prefix = spring.datasource.[xxx]
 *
 */

public class DataSourceConfig {
    protected String dataSourceId;
    protected String url ;
    protected String user ;
    protected String password ;
    /**
     * 初始化时建立物理连接的个数。 默认10
     * 初始化发生在显示调用init方法，或者第一次getConnection时
     */
    protected int initialSize = 10 ;
    /**
     * 最大连接池数量 默认300
     */
    protected int maxActive = 300 ;
    /**
     * 最小连接池数量 默认10
     */
    protected int minIdle = 10 ;
    /**
     * 获取连接时最大等待时间，单位毫秒。
     * 配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，
     * 如果需要可以通过配置useUnfairLock属性为true使用非公平锁。
     */
    protected long maxWait = 30000L;
    /**
     * 是否缓存preparedStatement，也就是PSCache。PSCache对支持游标的数据库性能提升巨大，比如说oracle。
     * 在mysql下建议关闭。
     */
    protected boolean poolPreparedStatements = false;
    /**
     * 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。
     * 在Druid中，不会存在Oracle下PSCache占用内存过多的问题，可以把这个数值配置大一些，比如说100
     */
    protected int maxOpenPreparedStatements = 0 ;
    /**
     * 用来检测连接是否有效的sql，要求是一个查询语句。
     * 如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会其作用。
     */
    protected String validationQuery = "select 1";
    /**
     * 申请连接时执行validationQuery检测连接是否有效，
     * 做了这个配置会降低性能。
     */
    protected boolean testOnBorrow = false;
    /**
     * 归还连接时执行validationQuery检测连接是否有效，
     * 做了这个配置会降低性能
     */
    protected boolean testOnReturn = false;
    /**
     * 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，
     * 如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
     */
    protected boolean testWhileIdle = true ;

    /**
     *  超时回收连接 开启 默认 开启
     */
    protected boolean removeAbandoned = true;
    /**
     * 回收连接 超时时间  默认 ： 5 分钟 = 300 000L 毫秒
     */
    protected long removeAbandonedTimeoutMillis = 300000L ;

    public DataSourceConfig(String dataSourceId, String url, String user, String password) {
        this.dataSourceId = dataSourceId;
        this.url = url;
        this.user = user;
        this.password = password;
    }


    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public void setPoolPreparedStatements(boolean poolPreparedStatements) {
        this.poolPreparedStatements = poolPreparedStatements;
    }

    public void setMaxOpenPreparedStatements(int maxOpenPreparedStatements) {
        this.maxOpenPreparedStatements = maxOpenPreparedStatements;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public String getDataSourceId() {
        return dataSourceId;
    }



    public long getRemoveAbandonedTimeoutMillis() {
        return removeAbandonedTimeoutMillis;
    }

    public boolean isRemoveAbandoned() {
        return removeAbandoned;
    }

    public void setRemoveAbandoned(boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    public void setRemoveAbandonedTimeoutMillis(long removeAbandonedTimeoutMillis) {
        this.removeAbandonedTimeoutMillis = removeAbandonedTimeoutMillis;
    }

    /**
     * 返回格式化好的 string 以便检查配置
     * @return
     */
    public String showConfigString(){
        StringBuilder stringBuilder = new StringBuilder("DataSource Config :{\n");
        stringBuilder.append("\t dataSourceId                   :" ).append(dataSourceId).append("  [可以理解为数据库名 ]\n");
        stringBuilder.append("\t initialSize                    :" ).append(initialSize ).append("  [初始化时建立物理连接的个数 ]\n");
        stringBuilder.append("\t maxActive                      :" ).append(maxActive ).append("  [ 最大连接池数量 ]\n");
        stringBuilder.append("\t minIdle                        :" ).append(minIdle ).append("  [最小连接池数量  ]\n");
        stringBuilder.append("\t maxWait                        :" ).append(maxWait  ).append("  [获取连接时最大等待时间 配置了maxWait之后，缺省启用公平锁，并发效率会有所下降， 如果需要可以通过配置useUnfairLock属性为true使用非公平锁  ]\n");
        stringBuilder.append("\t poolPreparedStatements         :" ).append(poolPreparedStatements  ).append("  [是否缓存preparedStatement 在mysql下建议关闭。 ]\n");
        stringBuilder.append("\t maxOpenPreparedStatements      :" ).append(maxOpenPreparedStatements   ).append("  [要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。 ]\n");
        stringBuilder.append("\t validationQuery                :" ).append(validationQuery ).append("  [ 用来检测连接是否有效的sql，要求是一个查询语句。 ]\n");
        stringBuilder.append("\t testOnBorrow                   :" ).append(testOnBorrow  ).append("  [申请连接时执行validationQuery检测连接是否有效  做了这个配置会降低性能]\n");
        stringBuilder.append("\t testOnReturn                   :" ).append(testOnReturn  ).append("  [ 归还连接时执行validationQuery检测连接是否有效 做了这个配置会降低性能]\n");
        stringBuilder.append("\t testWhileIdle                  :" ).append(testWhileIdle ).append("  [建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测 ]\n");
        stringBuilder.append("\t removeAbandoned                :" ).append(this.removeAbandoned ).append("  [建议配置为true，开启超时回收连接功能 ]\n");
        stringBuilder.append("\t removeAbandonedTimeoutMillis   :" ).append(this.removeAbandonedTimeoutMillis ).append("  [超时时长，超过时间后回收连接]\n");
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }



    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public long getMaxWait() {
        return maxWait;
    }

    public boolean isPoolPreparedStatements() {
        return poolPreparedStatements;
    }

    public int getMaxOpenPreparedStatements() {
        return maxOpenPreparedStatements;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }
}
