package com.sonsure.dustman.common.utility;

import java.io.IOException;
import java.io.InputStream;

/**
 * 资源接口 - 简化版本，不依赖Spring
 * 
 * @author selfly
 */
public interface GenericResource {
    
    /**
     * 获取资源的输入流
     * 
     * @return 输入流
     * @throws IOException IO异常
     */
    InputStream getInputStream() throws IOException;
    
    /**
     * 获取资源的文件名
     * 
     * @return 文件名
     */
    String getFilename();
}

