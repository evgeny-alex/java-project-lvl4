package hexlet.code.controllers;

import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;
import io.ebean.PagedList;
import java.util.Map;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import kong.unirest.Unirest;
import kong.unirest.HttpResponse;

import hexlet.code.domain.query.QUrl;
import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrlCheck;
import hexlet.code.domain.UrlCheck;

public final class UrlController {

    private static final int ROW_PER_PAGE = 10;

    public static Handler listUrls = ctx -> {
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1) - 1;

        PagedList<Url> pagedUrls = new QUrl()
                .setFirstRow(page * ROW_PER_PAGE)
                .setMaxRows(ROW_PER_PAGE)
                .orderBy()
                .id.asc()
                .findPagedList();

        List<Url> urls = pagedUrls.getList();

        Map<Long, UrlCheck> urlChecks = new QUrlCheck()
                .url.id.asMapKey()
                .orderBy()
                .createdAt.desc()
                .findMap();

        int lastPage = pagedUrls.getTotalPageCount() + 1;
        int currentPage = pagedUrls.getPageIndex() + 1;
        List<Integer> pages = IntStream
                .range(1, lastPage)
                .boxed()
                .collect(Collectors.toList());

        ctx.attribute("urls", urls);
        ctx.attribute("urlChecks", urlChecks);
        ctx.attribute("pages", pages);
        ctx.attribute("currentPage", currentPage);
        ctx.render("urls/index.html");
    };

    public static Handler createUrl = ctx -> {
        String inputUrl = ctx.formParam("url");

        URL parsedUrl;
        try {
            parsedUrl = new URL(inputUrl);
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
            return;
        }

        String normalizedUrl = String
                .format(
                        "%s://%s%s",
                        parsedUrl.getProtocol(),
                        parsedUrl.getHost(),
                        parsedUrl.getPort() == -1 ? "" : ":" + parsedUrl.getPort()
                )
                .toLowerCase();

        Url url = new QUrl()
                .name.equalTo(normalizedUrl)
                .findOne();

        if (url != null) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "info");
        } else {
            Url newUrl = new Url(normalizedUrl);
            newUrl.save();
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flash-type", "success");
        }

        ctx.redirect("/urls");
    };

    public static Handler showUrl = ctx -> {
        int id = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);

        Url url = new QUrl()
                .id.equalTo(id)
                .urlChecks.fetch()
                .orderBy()
                .urlChecks.createdAt.desc()
                .findOne();

        if (url == null) {
            throw new NotFoundResponse();
        }

        ctx.attribute("url", url);
        ctx.render("urls/show.html");
    };

    public static Handler checkUrl = ctx -> {
        int id = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);

        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        if (url == null) {
            throw new NotFoundResponse();
        }

        try {
            HttpResponse<String> response = Unirest.get(url.getName()).asString();
            Document doc = Jsoup.parse(response.getBody());

            int statusCode = response.getStatus();
            String title = doc.title();
            Element h1Element = doc.selectFirst("h1");
            String h1 = h1Element == null ? "" : h1Element.text();
            Element descriptionElement = doc.selectFirst("meta[name=description]");
            String description = descriptionElement == null ? "" : descriptionElement.attr("content");

            UrlCheck newUrlCheck = new UrlCheck(statusCode, title, h1, description);
            url.getUrlChecks().add(newUrlCheck);
            url.save();

            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flash-type", "success");
        } catch (Exception e) {
            ctx.sessionAttribute("flash", e.getMessage());
            ctx.sessionAttribute("flash-type", "danger");
        }

        ctx.redirect("/urls/" + url.getId());
    };
}
