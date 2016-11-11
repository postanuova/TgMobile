package org.teenguard.child.dbdao;

import java.util.ArrayList;

/**
 * Created by chris on 09/11/16.
 */

public interface InterfaceDbDAO {

    public ArrayList getList();

    public ArrayList getList(String whereSql);

    public boolean delete(String idList);

    public boolean delete();

}
