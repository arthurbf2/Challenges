import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StarWarsChallenge {
	private static HttpURLConnection connection;
	private static ArrayList<JSONObject> lista = new ArrayList<JSONObject>();

	public static void findTheShip(String obj) throws MalformedURLException {
		JSONObject pagina = new JSONObject(obj);
		boolean flag = false; // para que pare de buscar após a falcon for achada
		try {
			if (pagina.getString("next") != null) {
				JSONArray array = pagina.getJSONArray("results");
				for (int i = 0; i < array.length(); i++) {
					JSONObject nave = array.getJSONObject(i);
					if (nave.getString("name").equals("Millennium Falcon")) {
						int num = nave.getJSONArray("pilots").length();
						System.out.println("The Millenium Falcon has had " + num + " pilots.");
						for (int j = 0; j < num; j++) {
							conectar(new URL(nave.getJSONArray("pilots").getString(j)), "pilotos");
						}
						System.out.println("\n");
						flag = true;
						break;
					}
				}
				if (!flag)
					conectar(new URL(pagina.getString("next")), "ship");
			}
		} catch (JSONException e) {

		}
	}

	public static void printaNomePilotos(String obj) throws MalformedURLException, JSONException {
		JSONObject pilot = new JSONObject(obj);
		System.out.println(pilot.getString("name") + " has piloted the Millennium Falcon.");
	}

	public static void addPersonagens(String obj) throws MalformedURLException, JSONException {
		JSONObject pagina = new JSONObject(obj);
		try {
			if (pagina.getString("next") != null) {
				JSONArray array = pagina.getJSONArray("results");
				for (int i = 0; i < array.length(); i++) {
					JSONObject pessoa = array.getJSONObject(i);
					lista.add(pessoa);
				}
				conectar(new URL(pagina.getString("next")), "personagens");
			}
		} catch (JSONException e) {

		}
	}

	public static void conectar(URL url, String chave) {
		BufferedReader reader;
		String line;
		StringBuffer responseContent = new StringBuffer();
		try {
			// URL url = new URL("https://swapi.dev/api/people/?page=1");

			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);

			int status = connection.getResponseCode();
			// System.out.println(status); // 200
			if (status > 299) {
				reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
				while ((line = reader.readLine()) != null) {
					responseContent.append(line);
				}
				reader.close();
			} else {
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				while ((line = reader.readLine()) != null) {
					responseContent.append(line);
				}
				reader.close();
			}
			if (chave.equals("personagens"))
				addPersonagens(responseContent.toString());
			else if (chave.equals("ship"))
				findTheShip(responseContent.toString());
			else
				printaNomePilotos(responseContent.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			connection.disconnect();
		}
	}

	public static ArrayList<JSONObject> ordenaLista(ArrayList<JSONObject> lista) {
		JSONObject temp;
		for (int i = 1; i < lista.size(); i++) {
			for (int j = 0; j < i; j++) {
				int itemi = lista.get(i).getJSONArray("films").length();
				int itemj = lista.get(j).getJSONArray("films").length();
				if (itemi > itemj) {
					temp = lista.get(i);
					lista.set(i, lista.get(j));
					lista.set(j, temp);
				}
			}
		}
		return lista;
	}

	public static void printaLista(ArrayList<JSONObject> lista) {
		int num = 0;
		int i = 0;
		for (JSONObject item : lista) {
			if (i > 9) // mostrar apenas os 10 primeiros
				break;
			num = item.getJSONArray("films").length();
			System.out.println(item.getString("name") + " shows up in " + num + " movies.");
			i++;
		}
	}

	public static void main(String[] args) throws JSONException, IOException {
		URL url1 = new URL("https://swapi.dev/api/people/?page=1");
		conectar(url1, "personagens");
		URL url2 = new URL("https://swapi.dev/api/starships/");
		conectar(url2, "ship");
		printaLista(ordenaLista(lista));
	}
}
