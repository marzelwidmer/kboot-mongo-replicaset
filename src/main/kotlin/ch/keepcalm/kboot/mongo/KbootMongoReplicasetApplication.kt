package ch.keepcalm.kboot.mongo

import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.datetime.LocalDate
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.MediaTypes
import org.springframework.hateoas.config.EnableHypermediaSupport
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder
import org.springframework.hateoas.support.WebStack
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.kotlin.core.publisher.toFlux
import javax.annotation.PostConstruct

@SpringBootApplication
class KbootMongoReplicasetApplication

fun main(args: Array<String>) {
    runApplication<KbootMongoReplicasetApplication>(*args)
}


@RestController
@RequestMapping(produces = [MediaTypes.HAL_JSON_VALUE])
@EnableHypermediaSupport(stacks = [WebStack.WEBFLUX], type = [EnableHypermediaSupport.HypermediaType.HAL])
class IndexResource() {
    companion object REL {
        const val REL_SPRING_INITIALIZR = "start-spring"
    }

    @GetMapping("/")
    suspend fun index(): EntityModel<Unit> {
        return EntityModel.of(Unit, WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(IndexResource::class.java).index()).withSelfRel().toMono().awaitSingle())
            .add(Link.of("http://start.spring.io").withRel(REL_SPRING_INITIALIZR))
    }
}


@Component
class DataLoader(private val repository: MovieRepository) {

    @PostConstruct
    fun load() =
        repository.deleteAll().thenMany(

            listOf(
                Pair("Cry Macho", LocalDate(2021, 1, 1)),
                Pair("Richard Jewell", LocalDate(2019, 1, 1)),
                Pair("The Mule", LocalDate(2018, 1, 1)),
                Pair("The 15:17 to Paris", LocalDate(2018, 1, 1)),
                Pair("Sad Hill Unearthed", LocalDate(2017, 1, 1)),
                Pair("Indian Horse", LocalDate(2017, 1, 1)),
                Pair("Sully", LocalDate(2016, 1, 1)),

                ).toFlux()
                .map {
                    Movie(title = it.first, releaseDate = it.second)
                }
        )
            .flatMap { repository.save(it) }
            .thenMany(repository.findAll())
            .subscribe { println(it) }
}

@Document
data class Movie(val title: String, val releaseDate: LocalDate)
interface MovieRepository : ReactiveCrudRepository<Movie, String>

