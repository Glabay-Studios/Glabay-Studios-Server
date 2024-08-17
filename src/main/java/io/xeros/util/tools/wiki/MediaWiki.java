package io.xeros.util.tools.wiki;

import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.tuple.Pair;


public class MediaWiki
{

    public static final Gson GSON = new GsonBuilder()
            // .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    private static final class WikiInnerResponse
    {
        Map<String, String> wikitext;
    }

    private static final class WikiResponse
    {
        WikiInnerResponse parse;
    }

    private final OkHttpClient client = new OkHttpClient();
    private final OkHttpClient clientNoRedirect = client.newBuilder()
            .followRedirects(false)
            .followSslRedirects(false)
            .build();


    public HttpUrl getBase() {
        return base;
    }

    private final HttpUrl base;

    public MediaWiki(final String base)
    {
        this.base = HttpUrl.parse(base);
    }

    public Pair<String, String> getSpecialLookupData(final String type, final int id, final int section)
    {
        final HttpUrl url = base.newBuilder()
                .addPathSegment("w")
                .addPathSegment("Special:Lookup")
                .addQueryParameter("type", type)
                .addQueryParameter("id", String.valueOf(id))
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .build();

        try (final Response response = clientNoRedirect.newCall(request).execute())
        {
            if (response.isRedirect())
            {
                String responseHeaderLocation = response.header("Location");

                if (responseHeaderLocation == null)
                {
                    return null;
                }

                final String page = responseHeaderLocation
                        .replace(base.newBuilder().addPathSegment("w").build().toString() + "/", "");

                return getPageData(page, section);
            }
            else
            {
                System.out.println("unsuccessful");
                if (response.code() == 429)
                {
                    Thread.sleep(2500);
                    return getSpecialLookupData(type, id, section);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
            return null;
        }

        return null;
    }

    public Pair<String, String> getPageData(String page, int section)
    {
        // decode html encoded page name
        // ex: Mage%27s book -> Mage's_book
        try
        {
            page = URLDecoder.decode(page, StandardCharsets.UTF_8.name());
        }
        catch (UnsupportedEncodingException e)
        {
            // do nothing, keep page the same
        }

        final HttpUrl.Builder urlBuilder = base.newBuilder()
                .addPathSegment("api.php")
                .addQueryParameter("action", "parse")
                .addQueryParameter("format", "json")
                .addQueryParameter("prop", "wikitext")
                .addQueryParameter("redirects", "true")
                .addQueryParameter("page", page.replaceAll(" ", "_"));

        if (section != -1)
        {
            urlBuilder.addQueryParameter("section", String.valueOf(section));
        }

        final HttpUrl url = urlBuilder.build();

        final Request request = new Request.Builder()
                .url(url)
                .build();

        try (final Response response = client.newCall(request).execute())
        {
            if (response.isSuccessful())
            {
                ResponseBody responseBody = response.body();

                if (responseBody == null)
                {
                    return null;
                }

                final InputStream in = responseBody.byteStream();
                return Pair.of(page.replaceAll(" ", "_"), GSON.fromJson(new InputStreamReader(in), WikiResponse.class).parse.wikitext.get("*"));
            }
            else
            {

                if (response.code() == 429)
                {
                    Thread.sleep(2500);
                    return getPageData(page, section);
                }
            }
        }
        catch (Exception e)
        {

            return null;
        }

        return null;
    }

    private static String fixIndex(final String base, final Integer index)
    {
        return index == 0 ? base : base + index;
    }


}