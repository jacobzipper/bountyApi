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

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.http.auth.AuthenticationException;

import java.io.UnsupportedEncodingException;
import java.util.Map;


/**
 * Bean that generates and process vehicles.
 */
public class VehicleBean {
    // Hack for edit vehicle to bypass sql overwriting body
    private static String storedString = "";

    /**
     * Generates a new order structured as a {@link Map}
     * @param id the id of the new vehicle
     * @param name the name of the new vehicle
     */
    public Vehicle generateVehicle(String id, String name) {
        if (name == null) {
            name = storedString;
        }
        Vehicle vehicle = new Vehicle();
        vehicle.setName(name);
        vehicle.setId(id);
        return vehicle;
    }

    /**
     * Generates a new order structured as a {@link Map}
     * @param name new name for vehicle
     */
    public Vehicle generateVehicle(String name) {
        Vehicle vehicle = new Vehicle();
        vehicle.setName(name);
        return vehicle;
    }

    /**
     * Hack for editing vehicle that bypasses a quirk of camel where body is overwritten
     * @param _storedString name to store
     */
    public void setStoredString(String _storedString) {
        storedString = _storedString;
    }

    /**
     * Authentication method that validates jwt tokens
     * @param token the jwt token to validate
     * @throws AuthenticationException
     * @throws UnsupportedEncodingException
     */
    public void authentication(String token) throws AuthenticationException, UnsupportedEncodingException {
        Algorithm algorithm = Algorithm.HMAC256("bounty0xsecret");
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("bounty0x")
                .withClaim("admin", true)
                .build(); //Reusable verifier instance
        DecodedJWT jwt = verifier.verify(token);
        if (jwt.getClaim("admin").asBoolean()) {
            return;
        } else {
            throw new AuthenticationException("Credentials incorrect. Use the proper jwt.");
        }
    }
}
