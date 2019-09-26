package com.github.casside.cas.support;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class ClientServer {

    /**
     * service name
     */
    String name;

    /**
     * visit URL
     */
    String url;

}