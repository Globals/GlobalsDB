package com.intersys.gds.noschema;

import com.intersys.globals.impl.ConnectionImpl;

/** <CODE>NoSchemaConnection</CODE> extends Globals Connection from
 * which it inherits all basic communication entry points. In
 * addition to that, it also servers as a NoSchemaMap factory.
 */

public class NoSchemaConnection extends ConnectionImpl {


    /** NoSchemaMap Factory.
     *
     * @param name schema name
     *
     * @return NoSchemaMap NoSchemaMap
     *
    */
    public NoSchemaMap getNoSchemaMap(String name) {
        return new NoSchemaMap(name,this);
    }

 }