
package com.example.myfirstapp.model

enum class SuspeitaMorte(
    val descricao: String,
    val categoria: CategoriaMortalidade
) {
    // Doenças infecciosas e parasitárias

    TRISTEZA_PARASITARIA_BOVINA(
        "Tristeza parasitária bovina (TPB)",
        CategoriaMortalidade.DOENCAS_INFECCIOSAS_PARASITARIAS
    ),

    MANQUEIRA(
        "Manqueira",
        CategoriaMortalidade.DOENCAS_INFECCIOSAS_PARASITARIAS
    ),

    TETANO(
        "Tétano",
        CategoriaMortalidade.DOENCAS_INFECCIOSAS_PARASITARIAS
    ),

    RAIVA_BOVINA(
        "Raiva bovina",
        CategoriaMortalidade.DOENCAS_INFECCIOSAS_PARASITARIAS
    ),

    PNEUMONIA(
        "Problemas respiratórios / pneumonia",
        CategoriaMortalidade.DOENCAS_INFECCIOSAS_PARASITARIAS
    ),

    MASTITE_GRAVE(
        "Mastite grave",
        CategoriaMortalidade.DOENCAS_INFECCIOSAS_PARASITARIAS
    ),

    // Intoxicações e causas tóxicas

    INTOXICACAO_POR_PLANTAS(
        "Intoxicação por plantas",
        CategoriaMortalidade.INTOXICACOES
    ),

    INTOXICACAO_POR_SUBSTANCIAS(
        "Intoxicação por substâncias tóxicas",
        CategoriaMortalidade.INTOXICACOES
    ),

    BOTULISMO(
        "Botulismo",
        CategoriaMortalidade.INTOXICACOES
    ),

    // Acidentes e causas traumáticas

    ESMAGAMENTO(
        "Esmagamento",
        CategoriaMortalidade.ACIDENTES_TRAUMATICOS
    ),

    QUEDA(
        "Queda",
        CategoriaMortalidade.ACIDENTES_TRAUMATICOS
    ),

    ATROPELAMENTO(
        "Atropelamento",
        CategoriaMortalidade.ACIDENTES_TRAUMATICOS
    ),

    AFOGAMENTO(
        "Afogamento",
        CategoriaMortalidade.ACIDENTES_TRAUMATICOS
    ),

    // Predação e animais peçonhentos

    ATAQUE_DE_ONCA(
        "Ataque de onça",
        CategoriaMortalidade.PREDACAO_E_ANIMAIS_PECONHENTOS
    ),

    PICADA_DE_COBRA(
        "Picada de cobra",
        CategoriaMortalidade.PREDACAO_E_ANIMAIS_PECONHENTOS
    ),

    // Distúrbios digestivos e metabólicos

    TIMPANISMO(
        "Timpanismo",
        CategoriaMortalidade.DISTURBIOS_DIGESTIVOS_METABOLICOS
    ),

    ACIDOSE_RUMINAL(
        "Acidose ruminal",
        CategoriaMortalidade.DISTURBIOS_DIGESTIVOS_METABOLICOS
    ),

    // Outras situações

    CAUSA_NAO_IDENTIFICADA(
        "Causa não identificada",
        CategoriaMortalidade.OUTRAS_CAUSAS
    ),

    OUTRO(
        "Outro",
        CategoriaMortalidade.OUTRAS_CAUSAS
    )
}