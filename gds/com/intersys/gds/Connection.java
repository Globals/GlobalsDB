package com.intersys.gds;

import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;
import com.intersys.globals.impl.ConnectionImpl;

/** <CODE>Connection</CODE> extends Globals Connection from which it
 * inherits all basic communication entry points. In addition to that
 * it also servers as a DocumentMap factory, and provides schema
 * management methods.
 */

public class Connection extends ConnectionImpl {

   /** DocumentMap Factory.
    *
    * @param name schema name
    *
    * @return DocumentMap DocumentMap
    *
    */
    public DocumentMap getDocumentMap(String name) {
        return new DocumentMap(name,this);
    }

   /** Saves a DocumentTypes.
    *
    * @param documentType DocumentType to be saved
    *
    */
    public void saveDocumentType(DocumentType documentType) {
        NodeReference global = createNodeReference("Schema");
        ValueList list = createList();
        documentType.store(this,list);
        global.set(list,documentType.name);

        list.clear();
        global = createNodeReference("Indices");
        global.appendSubscript(documentType.name);
        documentType.storeIndices(global,list);
    }

   /** Deletes a Document Type.
    *
    * @param name name of the DocumentType to be deleted
    *
    */
    public void deleteDocumentType(String name) {
        NodeReference global = createNodeReference("Schema");
        global.killNode(name);
        global = createNodeReference("Indices");
        global.killNode(name);
    }
}
