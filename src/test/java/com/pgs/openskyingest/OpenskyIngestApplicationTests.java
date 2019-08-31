package com.pgs.openskyingest;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OpenskyIngestApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Test
	public void getIcao() throws ClientProtocolException, IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet("https://opensky-network.org/api/metadata/aircraft/list?n=50&p=1&q=N15GX&sc=&sd=");

		CloseableHttpResponse response = client.execute(httpGet);
		Assert.assertEquals(200, response.getStatusLine().getStatusCode());
		client.close();
	}

}
