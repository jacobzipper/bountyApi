/**************************************************************************************
 http://code.google.com/a/apache-extras.org/p/camel-extra

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.


 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 02110-1301, USA.

 http://www.gnu.org/licenses/gpl-2.0-standalone.html
 ***************************************************************************************/
package bountyApi;

/**
 *
 */
public class Vehicle {
    private String id = "";
    private int k;
    private String name;

    /**
     * @return vehicle name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name new name for vehicle
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return vehicle id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id new id for vehicle
     */
    public void setId(String id) {
        this.id = id;

    }

    /**
     * @return vehicle k (primary key)
     */
    public int getK() {
        return k;
    }

    /**
     * @param k new vehicle k (hibernate handles this)
     */
    public void setK(int k) {
        this.k = k;

        // Hack for auto setting id's
        if (id.equals("") || id.equals("0")) setId();
    }

    /**
     * Hack for auto setting id's with hibernate
     */
    public void setId() {
        this.id = this.k + "";
    }
}
