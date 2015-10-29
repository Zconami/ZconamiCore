package com.zconami.Core.domain;

public interface EntityObserver<E extends Entity> {

    void entityChanged(E entity);

    void entityRemoved(E entity);

}
