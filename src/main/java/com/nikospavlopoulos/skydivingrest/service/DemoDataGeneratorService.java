package com.nikospavlopoulos.skydivingrest.service;

import com.nikospavlopoulos.skydivingrest.core.events.UserRegisteredEvent;
import com.nikospavlopoulos.skydivingrest.model.Jump;
import com.nikospavlopoulos.skydivingrest.model.User;
import com.nikospavlopoulos.skydivingrest.model.static_data.Aircraft;
import com.nikospavlopoulos.skydivingrest.model.static_data.Dropzone;
import com.nikospavlopoulos.skydivingrest.model.static_data.Jumptype;
import com.nikospavlopoulos.skydivingrest.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("demo")
public class DemoDataGeneratorService {

    private final JumpRepository jumpRepository;
    private final UserRepository userRepository;
    private final AircraftRepository aircraftRepository;
    private final DropzoneRepository dropzoneRepository;
    private final JumptypeRepository jumptypeRepository;

    @EventListener
    @Transactional
    public void handleUserRegistered(UserRegisteredEvent event) {

        User user = userRepository.findById(event.userId()).orElseThrow();

        List<Aircraft> allAircraft = aircraftRepository.findAll();
        List<Dropzone> allDropzones = dropzoneRepository.findAll();
        List<Jumptype> allJumptypes = jumptypeRepository.findAll();

        // Define 5 demo data template Jumps

        List<Jump> demoJumps = List.of(
                Jump.builder()
                        .user(user)
                        .altitude(12024)
                        .freeFallDuration(59)
                        .aircraft(allAircraft.get(3 % allAircraft.size()))
                        .dropzone(allDropzones.get(2 % allDropzones.size()))
                        .jumptype(allJumptypes.get(0))
                        .jumpDate(LocalDateTime.now().minusDays(8))
                        .jumpNotes("First Jump on the new rig!")
                        .build(),
                Jump.builder()
                        .user(user)
                        .altitude(13500)
                        .freeFallDuration(62)
                        .aircraft(allAircraft.get(1 % allAircraft.size()))
                        .dropzone(allDropzones.get(1 % allDropzones.size()))
                        .jumptype(allJumptypes.get(1 % allJumptypes.size()))
                        .jumpDate(LocalDateTime.now().minusDays(6))
                        .jumpNotes("Beautiful sunset load, tracking was on point.")
                        .build(),
                Jump.builder()
                        .user(user)
                        .altitude(10000)
                        .freeFallDuration(45)
                        .aircraft(allAircraft.get(0))
                        .dropzone(allDropzones.get(2 % allDropzones.size()))
                        .jumptype(allJumptypes.get(2 % allJumptypes.size()))
                        .jumpDate(LocalDateTime.now().minusDays(4))
                        .jumpNotes("Quick hop and pop to practice canopy accuracy.")
                        .build(),
                Jump.builder()
                        .user(user)
                        .altitude(14000)
                        .freeFallDuration(65)
                        .aircraft(allAircraft.get(2 % allAircraft.size()))
                        .dropzone(allDropzones.get(0))
                        .jumptype(allJumptypes.get(3 % allJumptypes.size()))
                        .jumpDate(LocalDateTime.now().minusDays(2))
                        .jumpNotes("Fun 4-way scramble with the locals!")
                        .build(),
                Jump.builder()
                        .user(user)
                        .altitude(12500)
                        .freeFallDuration(55)
                        .aircraft(allAircraft.get(1 % allAircraft.size()))
                        .dropzone(allDropzones.get(1 % allDropzones.size()))
                        .jumptype(allJumptypes.get(0))
                        .jumpDate(LocalDateTime.now().minusHours(5))
                        .jumpNotes("Testing out the new GoPro mount, footage looks great.")
                        .build()
        );

        jumpRepository.saveAll(demoJumps);

    }

}
