package com.springlearning.domain.file.entity;

import com.springlearning.domain.file.entity.type.FileType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Column(name = "original_name")
    private String originalName;

    @NotNull
    private String url;

    @NotNull
    private String dir;

    @Enumerated(EnumType.STRING)
    private FileType fileType;
}
