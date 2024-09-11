package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosSerie(@JsonAlias("Title") String Titulo,
                         @JsonAlias("Actors") String Atores
                         ,@JsonAlias("Plot") String Sinopse,
@JsonAlias("Poster") String Poster,
                         @JsonAlias("Genre") String Genero,
                         @JsonAlias("totalSeasons") Integer totalTemporadas,
                         @JsonAlias("imdbRating") String Avaliacao ) {

}
