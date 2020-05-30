package com.galen.subscriber.core;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author shuaiys
 * @version 1.0
 * @package co.lvita.subscriber.core.filter
 * @description TODO
 * @date 2020-03-18 23:31
 */
@Data
public class ChangeDataEntity implements Serializable {
    private static final long serialVersionUID = -8745226041907147471L;

    private Map<String, Object> beforeColumns = new HashMap<>();

    private Map<String, Object> afterColumns = new HashMap<>();

    private Set<String> updateColumns = new HashSet<>();

}
