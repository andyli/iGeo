/*---

    iGeo - http://igeo.jp

    Copyright (c) 2002-2011 Satoru Sugihara

    This file is part of iGeo.

    iGeo is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as
    published by the Free Software Foundation, version 3.

    iGeo is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with iGeo.  If not, see <http://www.gnu.org/licenses/>.

---*/

package igeo.core;

import java.util.ArrayList;

/**
   Implementation of IDynamicObject. Only management of parent IObject.
   
   @author Satoru Sugihara
   @version 0.7.0.0
*/
public class IDynamicObjectBase implements IDynamicObject{
    
    public IObject parent=null;
    // implementation of IDynamicObject
    
    public IObject parent(){ return parent; }
    public IDynamicObjectBase parent(IObject parent){ this.parent=parent; return this; }    
    
    /* behavior definition of interaction with other dynamic objects.
       interaction happens between every two dynamic objects in a server.
       interact() is called for every combination of two but
       when A.interact(B) happens, B.interact(A) doesn't happen.
       Just once for each combination */
    //public void interact(IDynamicObject obj){}
    
    /** behavior definition of interaction with other dynamic objects.
	The server puts all dynamic objects including itself.
    */
    public void interact(ArrayList<IDynamicObject> dynamics){}
    /** behavior definition of updating dynamics in each time frame */
    public void update(){}
}