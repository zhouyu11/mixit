package mixit

import com.mongodb.ConnectionString
import com.samskivert.mustache.Mustache
import mixit.controller.*
import mixit.repository.PostRepository
import mixit.repository.EventRepository
import mixit.repository.TalkRepository
import mixit.repository.UserRepository
import mixit.util.*
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.context.support.registerBean
import org.springframework.beans.factory.getBean
import org.springframework.context.MessageSource
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactory
import org.springframework.web.reactive.result.view.mustache.MustacheResourceTemplateLoader
import org.springframework.web.reactive.result.view.mustache.MustacheViewResolver

fun context(port: Int?, hostname: String) = AnnotationConfigApplicationContext {
        environment.addPropertySource("application.properties")
        registerBean("messageSource") {
            ReloadableResourceBundleMessageSource().apply {
                setBasename("messages")
                setDefaultEncoding("UTF-8")
            }
        }
        registerBean {
            MustacheViewResolver().apply {
                val prefix = "classpath:/templates/"
                val suffix = ".mustache"
                val loader = MustacheResourceTemplateLoader(prefix, suffix)
                setPrefix(prefix)
                setSuffix(suffix)
                setCompiler(Mustache.compiler().escapeHTML(false).withLoader(loader))
                setModelCustomizer({ model, exchange ->  customizeModel(model, exchange, it.getBean<MessageSource>()) })
            }
        }
        registerBean { ReactiveMongoTemplate(SimpleReactiveMongoDatabaseFactory(
                ConnectionString(it.environment.getProperty("mongo.uri"))))
        }
        registerBean { ReactiveMongoRepositoryFactory(it.getBean<ReactiveMongoTemplate>()) }
        registerBean { ReactorNettyServer(hostname, port ?: it.environment.getProperty("server.port").toInt(), it.environment.getProperty("baseUri")) }

        registerBean { MarkdownConverter() }

        registerBean<UserRepository>()
        registerBean<EventRepository>()
        registerBean<TalkRepository>()
        registerBean<PostRepository>()

        registerBean<BlogController>()
        registerBean<UserController>()
        registerBean<EventController>()
        registerBean<TalkController>()
        registerBean<NewsController>()
        registerBean<GlobalController>()
        registerBean<RedirectController>()
}
